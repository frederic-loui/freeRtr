mod cmd;
mod common;
mod config;
mod pcap_cap;
mod sock;

fn main() {
    #[cfg(not(target_family = "unix"))]
    compile_error!("pcap_int can only be compiled for unix targets");

    let conf = config::Config::load();
    if conf.is_err() {
        eprintln!(
            "Error parsing arguments: {}\n{}",
            conf.unwrap_err(),
            config::usage()
        );
        return;
    }

    let conf = conf.unwrap();
    if conf.is_none() {
        return;
    }

    let conf = conf.unwrap();
    let stats = common::Stats::new();
    let mut pcap = unwrap_err!(pcap_cap::Pcap::open(&conf));
    let mut sock = unwrap_err!(sock::Sock::new(&conf));

    let rx_stats = stats.clone();
    let tx_stats = stats.clone();

    let if_tx = pcap.get_cap();
    let sock_tx = unwrap_err!(sock.get_writer());

    if conf.daemonize {
        if let Err(e) = unsafe { common::daemonize(pcap, sock, if_tx, sock_tx, rx_stats, tx_stats) }
        {
            eprintln!("Error while daemonizing: {e}");
        }
    } else {
        let mut cmd = cmd::Cmd::new();

        std::thread::spawn(move || pcap.pcap_loop(sock_tx, rx_stats));
        std::thread::spawn(move || sock.sock_loop(if_tx, tx_stats));

        cmd.cmd_loop(stats);
    }
}
