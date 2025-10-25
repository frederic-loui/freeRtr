description p4lang: ldp te over mpls mid

addrouter r1
int eth1 eth 0000.0000.1111 $1a$ $1b$
int eth2 eth 0000.0000.1111 $2b$ $2a$
!
vrf def v1
 rd 1:1
 label-mode per-prefix
 exit
vrf def v9
 rd 1:1
 exit
access-list test4
 deny 1 any all any all
 permit all any all any all
 exit
access-list test6
 deny 58 4321:: ffff:: all 4321:: ffff:: all
 permit all any all any all
 exit
int lo9
 vrf for v9
 ipv4 addr 10.10.10.227 255.255.255.255
 exit
int eth1
 vrf for v9
 ipv4 addr 10.11.12.254 255.255.255.0
 exit
int eth2
 exit
server dhcp4 eth1
 pool 10.11.12.1 10.11.12.99
 gateway 10.11.12.254
 netmask 255.255.255.0
 dns-server 10.10.10.227
 domain-name p4l
 static 0000.0000.2222 10.11.12.111
 interface eth1
 vrf v9
 exit
int lo0
 vrf for v1
 ipv4 addr 2.2.2.101 255.255.255.255
 ipv6 addr 4321::101 ffff:ffff:ffff:ffff:ffff:ffff:ffff:ffff
 mpls ldp4
 mpls ldp6
 mpls label4peer test4
 mpls label6peer test6
 exit
int lo1
 vrf for v1
 ipv4 addr 2.2.2.201 255.255.255.255
 ipv6 addr 4321::201 ffff:ffff:ffff:ffff:ffff:ffff:ffff:ffff
 exit
int sdn1
 vrf for v1
 ipv4 addr 1.1.1.1 255.255.255.0
 ipv6 addr 1234:1::1 ffff:ffff::
 ipv6 ena
 mpls enable
 mpls ldp4
 mpls ldp6
 exit
int sdn2
 vrf for v1
 ipv4 addr 1.1.2.1 255.255.255.0
 ipv6 addr 1234:2::1 ffff:ffff::
 ipv6 ena
 mpls enable
 mpls ldp4
 mpls ldp6
 exit
int sdn3
 vrf for v1
 ipv4 addr 1.1.3.1 255.255.255.0
 ipv6 addr 1234:3::1 ffff:ffff::
 ipv6 ena
 mpls enable
 mpls ldp4
 mpls ldp6
 exit
int sdn4
 vrf for v1
 ipv4 addr 1.1.4.1 255.255.255.0
 ipv6 addr 1234:4::1 ffff:ffff::
 ipv6 ena
 mpls enable
 mpls ldp4
 mpls ldp6
 exit
int tun11
 tun sou lo0
 tun dest 2.2.2.103
 tun domain 2.2.2.103
 tun vrf v1
 tun mod teldp
 vrf for v1
 ipv4 addr 1.1.11.1 255.255.255.0
 exit
int tun12
 tun sou lo0
 tun dest 4321::103
 tun domain 4321::103
 tun vrf v1
 tun mod teldp
 vrf for v1
 ipv6 addr 1234:11::1 ffff:ffff::
 exit
int tun21
 tun sou lo0
 tun dest 2.2.2.104
 tun domain 2.2.2.104
 tun vrf v1
 tun mod teldp
 vrf for v1
 ipv4 addr 1.1.12.1 255.255.255.0
 exit
int tun22
 tun sou lo0
 tun dest 4321::104
 tun domain 4321::104
 tun vrf v1
 tun mod teldp
 vrf for v1
 ipv6 addr 1234:12::1 ffff:ffff::
 exit
int tun31
 tun sou lo0
 tun dest 2.2.2.105
 tun domain 2.2.2.105
 tun vrf v1
 tun mod teldp
 vrf for v1
 ipv4 addr 1.1.13.1 255.255.255.0
 exit
int tun32
 tun sou lo0
 tun dest 4321::105
 tun domain 4321::105
 tun vrf v1
 tun mod teldp
 vrf for v1
 ipv6 addr 1234:13::1 ffff:ffff::
 exit
int tun41
 tun sou lo0
 tun dest 2.2.2.106
 tun domain 2.2.2.106
 tun vrf v1
 tun mod teldp
 vrf for v1
 ipv4 addr 1.1.14.1 255.255.255.0
 exit
int tun42
 tun sou lo0
 tun dest 4321::106
 tun domain 4321::106
 tun vrf v1
 tun mod teldp
 vrf for v1
 ipv6 addr 1234:14::1 ffff:ffff::
 exit
server p4lang p4
 interconnect eth2
 export-vrf v1
 export-port sdn1 1 10
 export-port sdn2 2 10
 export-port sdn3 3 10
 export-port sdn4 4 10
 export-port tun11 dynamic
 export-port tun12 dynamic
 export-port tun21 dynamic
 export-port tun22 dynamic
 export-port tun31 dynamic
 export-port tun32 dynamic
 export-port tun41 dynamic
 export-port tun42 dynamic
 vrf v9
 exit
ipv4 route v1 2.2.2.103 255.255.255.255 1.1.1.2
ipv4 route v1 2.2.2.104 255.255.255.255 1.1.2.2
ipv4 route v1 2.2.2.105 255.255.255.255 1.1.3.2
ipv4 route v1 2.2.2.106 255.255.255.255 1.1.4.2
ipv6 route v1 4321::103 ffff:ffff:ffff:ffff:ffff:ffff:ffff:ffff 1234:1::2
ipv6 route v1 4321::104 ffff:ffff:ffff:ffff:ffff:ffff:ffff:ffff 1234:2::2
ipv6 route v1 4321::105 ffff:ffff:ffff:ffff:ffff:ffff:ffff:ffff 1234:3::2
ipv6 route v1 4321::106 ffff:ffff:ffff:ffff:ffff:ffff:ffff:ffff 1234:4::2
ipv4 route v1 2.2.2.203 255.255.255.255 1.1.11.2
ipv4 route v1 2.2.2.204 255.255.255.255 1.1.12.2
ipv4 route v1 2.2.2.205 255.255.255.255 1.1.13.2
ipv4 route v1 2.2.2.206 255.255.255.255 1.1.14.2
ipv6 route v1 4321::203 ffff:ffff:ffff:ffff:ffff:ffff:ffff:ffff 1234:11::2
ipv6 route v1 4321::204 ffff:ffff:ffff:ffff:ffff:ffff:ffff:ffff 1234:12::2
ipv6 route v1 4321::205 ffff:ffff:ffff:ffff:ffff:ffff:ffff:ffff 1234:13::2
ipv6 route v1 4321::206 ffff:ffff:ffff:ffff:ffff:ffff:ffff:ffff 1234:14::2
!

addother r2 controller r1 v9 9080 - feature mpls
int eth1 eth 0000.0000.2222 $1b$ $1a$
int eth2 eth 0000.0000.2222 $2a$ $2b$
int eth3 eth 0000.0000.2222 $3a$ $3b$
int eth4 eth 0000.0000.2222 $4a$ $4b$
int eth5 eth 0000.0000.2222 $5a$ $5b$
int eth6 eth 0000.0000.2222 $6a$ $6b$
!
!

addrouter r3
int eth1 eth 0000.0000.3333 $3b$ $3a$
!
vrf def v1
 rd 1:1
 label-mode per-prefix
 exit
access-list test4
 deny 1 any all any all
 permit all any all any all
 exit
access-list test6
 deny 58 4321:: ffff:: all 4321:: ffff:: all
 permit all any all any all
 exit
int lo0
 vrf for v1
 ipv4 addr 2.2.2.103 255.255.255.255
 ipv6 addr 4321::103 ffff:ffff:ffff:ffff:ffff:ffff:ffff:ffff
 mpls ldp4
 mpls ldp6
 mpls label4peer test4
 mpls label6peer test6
 exit
int lo1
 vrf for v1
 ipv4 addr 2.2.2.203 255.255.255.255
 ipv6 addr 4321::203 ffff:ffff:ffff:ffff:ffff:ffff:ffff:ffff
 exit
int eth1
 vrf for v1
 ipv4 addr 1.1.1.2 255.255.255.0
 ipv6 addr 1234:1::2 ffff:ffff::
 mpls enable
 mpls ldp4
 mpls ldp6
 exit
int tun11
 tun sou lo0
 tun dest 2.2.2.101
 tun domain 2.2.2.101
 tun vrf v1
 tun mod teldp
 vrf for v1
 ipv4 addr 1.1.11.2 255.255.255.0
 exit
int tun12
 tun sou lo0
 tun dest 4321::101
 tun domain 4321::101
 tun vrf v1
 tun mod teldp
 vrf for v1
 ipv6 addr 1234:11::2 ffff:ffff::
 exit
int tun21
 tun sou lo0
 tun dest 2.2.2.104
 tun domain 2.2.2.101
 tun vrf v1
 tun mod teldp
 vrf for v1
 ipv4 addr 1.1.21.2 255.255.255.0
 exit
int tun22
 tun sou lo0
 tun dest 4321::104
 tun domain 4321::101
 tun vrf v1
 tun mod teldp
 vrf for v1
 ipv6 addr 1234:21::2 ffff:ffff::
 exit
int tun31
 tun sou lo0
 tun dest 2.2.2.105
 tun domain 2.2.2.101
 tun vrf v1
 tun mod teldp
 vrf for v1
 ipv4 addr 1.1.31.2 255.255.255.0
 exit
int tun32
 tun sou lo0
 tun dest 4321::105
 tun domain 4321::101
 tun vrf v1
 tun mod teldp
 vrf for v1
 ipv6 addr 1234:31::2 ffff:ffff::
 exit
int tun41
 tun sou lo0
 tun dest 2.2.2.106
 tun domain 2.2.2.101
 tun vrf v1
 tun mod teldp
 vrf for v1
 ipv4 addr 1.1.41.2 255.255.255.0
 exit
int tun42
 tun sou lo0
 tun dest 4321::106
 tun domain 4321::101
 tun vrf v1
 tun mod teldp
 vrf for v1
 ipv6 addr 1234:41::2 ffff:ffff::
 exit
ipv4 route v1 1.1.2.0 255.255.255.0 1.1.1.1
ipv4 route v1 1.1.3.0 255.255.255.0 1.1.1.1
ipv4 route v1 1.1.4.0 255.255.255.0 1.1.1.1
ipv6 route v1 1234:2:: ffff:ffff:: 1234:1::1
ipv6 route v1 1234:3:: ffff:ffff:: 1234:1::1
ipv6 route v1 1234:4:: ffff:ffff:: 1234:1::1
ipv4 route v1 2.2.2.101 255.255.255.255 1.1.1.1
ipv4 route v1 2.2.2.104 255.255.255.255 1.1.1.1
ipv4 route v1 2.2.2.105 255.255.255.255 1.1.1.1
ipv4 route v1 2.2.2.106 255.255.255.255 1.1.1.1
ipv6 route v1 4321::101 ffff:ffff:ffff:ffff:ffff:ffff:ffff:ffff 1234:1::1
ipv6 route v1 4321::104 ffff:ffff:ffff:ffff:ffff:ffff:ffff:ffff 1234:1::1
ipv6 route v1 4321::105 ffff:ffff:ffff:ffff:ffff:ffff:ffff:ffff 1234:1::1
ipv6 route v1 4321::106 ffff:ffff:ffff:ffff:ffff:ffff:ffff:ffff 1234:1::1
ipv4 route v1 2.2.2.201 255.255.255.255 1.1.11.1
ipv4 route v1 2.2.2.204 255.255.255.255 1.1.21.1
ipv4 route v1 2.2.2.205 255.255.255.255 1.1.31.1
ipv4 route v1 2.2.2.206 255.255.255.255 1.1.41.1
ipv6 route v1 4321::201 ffff:ffff:ffff:ffff:ffff:ffff:ffff:ffff 1234:11::1
ipv6 route v1 4321::204 ffff:ffff:ffff:ffff:ffff:ffff:ffff:ffff 1234:21::1
ipv6 route v1 4321::205 ffff:ffff:ffff:ffff:ffff:ffff:ffff:ffff 1234:31::1
ipv6 route v1 4321::206 ffff:ffff:ffff:ffff:ffff:ffff:ffff:ffff 1234:41::1
!

addrouter r4
int eth1 eth 0000.0000.4444 $4b$ $4a$
!
vrf def v1
 rd 1:1
 label-mode per-prefix
 exit
access-list test4
 deny 1 any all any all
 permit all any all any all
 exit
access-list test6
 deny 58 4321:: ffff:: all 4321:: ffff:: all
 permit all any all any all
 exit
int lo0
 vrf for v1
 ipv4 addr 2.2.2.104 255.255.255.255
 ipv6 addr 4321::104 ffff:ffff:ffff:ffff:ffff:ffff:ffff:ffff
 mpls ldp4
 mpls ldp6
 mpls label4peer test4
 mpls label6peer test6
 exit
int lo1
 vrf for v1
 ipv4 addr 2.2.2.204 255.255.255.255
 ipv6 addr 4321::204 ffff:ffff:ffff:ffff:ffff:ffff:ffff:ffff
 exit
int eth1
 vrf for v1
 ipv4 addr 1.1.2.2 255.255.255.0
 ipv6 addr 1234:2::2 ffff:ffff::
 mpls enable
 mpls ldp4
 mpls ldp6
 exit
int tun11
 tun sou lo0
 tun dest 2.2.2.101
 tun domain 2.2.2.101
 tun vrf v1
 tun mod teldp
 vrf for v1
 ipv4 addr 1.1.12.2 255.255.255.0
 exit
int tun12
 tun sou lo0
 tun dest 4321::101
 tun domain 4321::101
 tun vrf v1
 tun mod teldp
 vrf for v1
 ipv6 addr 1234:12::2 ffff:ffff::
 exit
ipv4 route v1 1.1.1.0 255.255.255.0 1.1.2.1
ipv4 route v1 1.1.3.0 255.255.255.0 1.1.2.1
ipv4 route v1 1.1.4.0 255.255.255.0 1.1.2.1
ipv6 route v1 1234:1:: ffff:ffff:: 1234:2::1
ipv6 route v1 1234:3:: ffff:ffff:: 1234:2::1
ipv6 route v1 1234:4:: ffff:ffff:: 1234:2::1
ipv4 route v1 2.2.2.101 255.255.255.255 1.1.2.1
ipv4 route v1 2.2.2.103 255.255.255.255 1.1.2.1
ipv4 route v1 2.2.2.105 255.255.255.255 1.1.2.1
ipv4 route v1 2.2.2.106 255.255.255.255 1.1.2.1
ipv6 route v1 4321::101 ffff:ffff:ffff:ffff:ffff:ffff:ffff:ffff 1234:2::1
ipv6 route v1 4321::103 ffff:ffff:ffff:ffff:ffff:ffff:ffff:ffff 1234:2::1
ipv6 route v1 4321::105 ffff:ffff:ffff:ffff:ffff:ffff:ffff:ffff 1234:2::1
ipv6 route v1 4321::106 ffff:ffff:ffff:ffff:ffff:ffff:ffff:ffff 1234:2::1
ipv4 route v1 2.2.2.201 255.255.255.255 1.1.12.1
ipv4 route v1 2.2.2.203 255.255.255.255 1.1.12.1
ipv4 route v1 2.2.2.205 255.255.255.255 1.1.12.1
ipv4 route v1 2.2.2.206 255.255.255.255 1.1.12.1
ipv6 route v1 4321::201 ffff:ffff:ffff:ffff:ffff:ffff:ffff:ffff 1234:12::1
ipv6 route v1 4321::203 ffff:ffff:ffff:ffff:ffff:ffff:ffff:ffff 1234:12::1
ipv6 route v1 4321::205 ffff:ffff:ffff:ffff:ffff:ffff:ffff:ffff 1234:12::1
ipv6 route v1 4321::206 ffff:ffff:ffff:ffff:ffff:ffff:ffff:ffff 1234:12::1
!

addrouter r5
int eth1 eth 0000.0000.5555 $5b$ $5a$
!
vrf def v1
 rd 1:1
 label-mode per-prefix
 exit
access-list test4
 deny 1 any all any all
 permit all any all any all
 exit
access-list test6
 deny 58 4321:: ffff:: all 4321:: ffff:: all
 permit all any all any all
 exit
int lo0
 vrf for v1
 ipv4 addr 2.2.2.105 255.255.255.255
 ipv6 addr 4321::105 ffff:ffff:ffff:ffff:ffff:ffff:ffff:ffff
 mpls ldp4
 mpls ldp6
 mpls label4peer test4
 mpls label6peer test6
 exit
int lo1
 vrf for v1
 ipv4 addr 2.2.2.205 255.255.255.255
 ipv6 addr 4321::205 ffff:ffff:ffff:ffff:ffff:ffff:ffff:ffff
 exit
int eth1
 vrf for v1
 ipv4 addr 1.1.3.2 255.255.255.0
 ipv6 addr 1234:3::2 ffff:ffff::
 mpls enable
 mpls ldp4
 mpls ldp6
 exit
int tun11
 tun sou lo0
 tun dest 2.2.2.101
 tun domain 2.2.2.101
 tun vrf v1
 tun mod teldp
 vrf for v1
 ipv4 addr 1.1.13.2 255.255.255.0
 exit
int tun12
 tun sou lo0
 tun dest 4321::101
 tun domain 4321::101
 tun vrf v1
 tun mod teldp
 vrf for v1
 ipv6 addr 1234:13::2 ffff:ffff::
 exit
ipv4 route v1 1.1.1.0 255.255.255.0 1.1.3.1
ipv4 route v1 1.1.2.0 255.255.255.0 1.1.3.1
ipv4 route v1 1.1.4.0 255.255.255.0 1.1.3.1
ipv6 route v1 1234:1:: ffff:ffff:: 1234:3::1
ipv6 route v1 1234:2:: ffff:ffff:: 1234:3::1
ipv6 route v1 1234:4:: ffff:ffff:: 1234:3::1
ipv4 route v1 2.2.2.101 255.255.255.255 1.1.3.1
ipv4 route v1 2.2.2.103 255.255.255.255 1.1.3.1
ipv4 route v1 2.2.2.104 255.255.255.255 1.1.3.1
ipv4 route v1 2.2.2.106 255.255.255.255 1.1.3.1
ipv6 route v1 4321::101 ffff:ffff:ffff:ffff:ffff:ffff:ffff:ffff 1234:3::1
ipv6 route v1 4321::103 ffff:ffff:ffff:ffff:ffff:ffff:ffff:ffff 1234:3::1
ipv6 route v1 4321::104 ffff:ffff:ffff:ffff:ffff:ffff:ffff:ffff 1234:3::1
ipv6 route v1 4321::106 ffff:ffff:ffff:ffff:ffff:ffff:ffff:ffff 1234:3::1
ipv4 route v1 2.2.2.201 255.255.255.255 1.1.13.1
ipv4 route v1 2.2.2.203 255.255.255.255 1.1.13.1
ipv4 route v1 2.2.2.204 255.255.255.255 1.1.13.1
ipv4 route v1 2.2.2.206 255.255.255.255 1.1.13.1
ipv6 route v1 4321::201 ffff:ffff:ffff:ffff:ffff:ffff:ffff:ffff 1234:13::1
ipv6 route v1 4321::203 ffff:ffff:ffff:ffff:ffff:ffff:ffff:ffff 1234:13::1
ipv6 route v1 4321::204 ffff:ffff:ffff:ffff:ffff:ffff:ffff:ffff 1234:13::1
ipv6 route v1 4321::206 ffff:ffff:ffff:ffff:ffff:ffff:ffff:ffff 1234:13::1
!

addrouter r6
int eth1 eth 0000.0000.6666 $6b$ $6a$
!
vrf def v1
 rd 1:1
 label-mode per-prefix
 exit
access-list test4
 deny 1 any all any all
 permit all any all any all
 exit
access-list test6
 deny 58 4321:: ffff:: all 4321:: ffff:: all
 permit all any all any all
 exit
int lo0
 vrf for v1
 ipv4 addr 2.2.2.106 255.255.255.255
 ipv6 addr 4321::106 ffff:ffff:ffff:ffff:ffff:ffff:ffff:ffff
 mpls ldp4
 mpls ldp6
 mpls label4peer test4
 mpls label6peer test6
 exit
int lo1
 vrf for v1
 ipv4 addr 2.2.2.206 255.255.255.255
 ipv6 addr 4321::206 ffff:ffff:ffff:ffff:ffff:ffff:ffff:ffff
 exit
int eth1
 vrf for v1
 ipv4 addr 1.1.4.2 255.255.255.0
 ipv6 addr 1234:4::2 ffff:ffff::
 mpls enable
 mpls ldp4
 mpls ldp6
 exit
int tun11
 tun sou lo0
 tun dest 2.2.2.101
 tun domain 2.2.2.101
 tun vrf v1
 tun mod teldp
 vrf for v1
 ipv4 addr 1.1.14.2 255.255.255.0
 exit
int tun12
 tun sou lo0
 tun dest 4321::101
 tun domain 4321::101
 tun vrf v1
 tun mod teldp
 vrf for v1
 ipv6 addr 1234:14::2 ffff:ffff::
 exit
ipv4 route v1 1.1.1.0 255.255.255.0 1.1.4.1
ipv4 route v1 1.1.2.0 255.255.255.0 1.1.4.1
ipv4 route v1 1.1.3.0 255.255.255.0 1.1.4.1
ipv6 route v1 1234:1:: ffff:ffff:: 1234:4::1
ipv6 route v1 1234:2:: ffff:ffff:: 1234:4::1
ipv6 route v1 1234:3:: ffff:ffff:: 1234:4::1
ipv4 route v1 2.2.2.101 255.255.255.255 1.1.4.1
ipv4 route v1 2.2.2.103 255.255.255.255 1.1.4.1
ipv4 route v1 2.2.2.104 255.255.255.255 1.1.4.1
ipv4 route v1 2.2.2.105 255.255.255.255 1.1.4.1
ipv6 route v1 4321::101 ffff:ffff:ffff:ffff:ffff:ffff:ffff:ffff 1234:4::1
ipv6 route v1 4321::103 ffff:ffff:ffff:ffff:ffff:ffff:ffff:ffff 1234:4::1
ipv6 route v1 4321::104 ffff:ffff:ffff:ffff:ffff:ffff:ffff:ffff 1234:4::1
ipv6 route v1 4321::105 ffff:ffff:ffff:ffff:ffff:ffff:ffff:ffff 1234:4::1
ipv4 route v1 2.2.2.201 255.255.255.255 1.1.14.1
ipv4 route v1 2.2.2.203 255.255.255.255 1.1.14.1
ipv4 route v1 2.2.2.204 255.255.255.255 1.1.14.1
ipv4 route v1 2.2.2.205 255.255.255.255 1.1.14.1
ipv6 route v1 4321::201 ffff:ffff:ffff:ffff:ffff:ffff:ffff:ffff 1234:14::1
ipv6 route v1 4321::203 ffff:ffff:ffff:ffff:ffff:ffff:ffff:ffff 1234:14::1
ipv6 route v1 4321::204 ffff:ffff:ffff:ffff:ffff:ffff:ffff:ffff 1234:14::1
ipv6 route v1 4321::205 ffff:ffff:ffff:ffff:ffff:ffff:ffff:ffff 1234:14::1
!



r1 tping 100 10 1.1.1.2 vrf v1
r1 tping 100 10 1234:1::2 vrf v1
r1 tping 100 10 1.1.2.2 vrf v1
r1 tping 100 10 1234:2::2 vrf v1
r1 tping 100 10 1.1.3.2 vrf v1
r1 tping 100 10 1234:3::2 vrf v1
r1 tping 100 10 1.1.4.2 vrf v1
r1 tping 100 10 1234:4::2 vrf v1

r3 tping 100 10 1.1.1.2 vrf v1
r3 tping 100 10 1234:1::2 vrf v1
r3 tping 100 10 1.1.2.2 vrf v1
r3 tping 100 10 1234:2::2 vrf v1
r3 tping 100 10 1.1.3.2 vrf v1
r3 tping 100 10 1234:3::2 vrf v1
r3 tping 100 10 1.1.4.2 vrf v1
r3 tping 100 10 1234:4::2 vrf v1

r4 tping 100 10 1.1.1.2 vrf v1
r4 tping 100 10 1234:1::2 vrf v1
r4 tping 100 10 1.1.2.2 vrf v1
r4 tping 100 10 1234:2::2 vrf v1
r4 tping 100 10 1.1.3.2 vrf v1
r4 tping 100 10 1234:3::2 vrf v1
r4 tping 100 10 1.1.4.2 vrf v1
r4 tping 100 10 1234:4::2 vrf v1

r5 tping 100 10 1.1.1.2 vrf v1
r5 tping 100 10 1234:1::2 vrf v1
r5 tping 100 10 1.1.2.2 vrf v1
r5 tping 100 10 1234:2::2 vrf v1
r5 tping 100 10 1.1.3.2 vrf v1
r5 tping 100 10 1234:3::2 vrf v1
r5 tping 100 10 1.1.4.2 vrf v1
r5 tping 100 10 1234:4::2 vrf v1

r6 tping 100 10 1.1.1.2 vrf v1
r6 tping 100 10 1234:1::2 vrf v1
r6 tping 100 10 1.1.2.2 vrf v1
r6 tping 100 10 1234:2::2 vrf v1
r6 tping 100 10 1.1.3.2 vrf v1
r6 tping 100 10 1234:3::2 vrf v1
r6 tping 100 10 1.1.4.2 vrf v1
r6 tping 100 10 1234:4::2 vrf v1

r1 tping 100 10 2.2.2.101 vrf v1 sou lo0
r1 tping 100 10 4321::101 vrf v1 sou lo0
r1 tping 100 10 2.2.2.103 vrf v1 sou lo0
r1 tping 100 10 4321::103 vrf v1 sou lo0
r1 tping 100 10 2.2.2.104 vrf v1 sou lo0
r1 tping 100 10 4321::104 vrf v1 sou lo0
r1 tping 100 10 2.2.2.105 vrf v1 sou lo0
r1 tping 100 10 4321::105 vrf v1 sou lo0
r1 tping 100 10 2.2.2.106 vrf v1 sou lo0
r1 tping 100 10 4321::106 vrf v1 sou lo0

r3 tping 100 10 2.2.2.101 vrf v1 sou lo0
r3 tping 100 10 4321::101 vrf v1 sou lo0
r3 tping 100 10 2.2.2.103 vrf v1 sou lo0
r3 tping 100 10 4321::103 vrf v1 sou lo0
r3 tping 100 10 2.2.2.104 vrf v1 sou lo0
r3 tping 100 10 4321::104 vrf v1 sou lo0
r3 tping 100 10 2.2.2.105 vrf v1 sou lo0
r3 tping 100 10 4321::105 vrf v1 sou lo0
r3 tping 100 10 2.2.2.106 vrf v1 sou lo0
r3 tping 100 10 4321::106 vrf v1 sou lo0

r4 tping 100 10 2.2.2.101 vrf v1 sou lo0
r4 tping 100 10 4321::101 vrf v1 sou lo0
r4 tping 100 10 2.2.2.103 vrf v1 sou lo0
r4 tping 100 10 4321::103 vrf v1 sou lo0
r4 tping 100 10 2.2.2.104 vrf v1 sou lo0
r4 tping 100 10 4321::104 vrf v1 sou lo0
r4 tping 100 10 2.2.2.105 vrf v1 sou lo0
r4 tping 100 10 4321::105 vrf v1 sou lo0
r4 tping 100 10 2.2.2.106 vrf v1 sou lo0
r4 tping 100 10 4321::106 vrf v1 sou lo0

r5 tping 100 10 2.2.2.101 vrf v1 sou lo0
r5 tping 100 10 4321::101 vrf v1 sou lo0
r5 tping 100 10 2.2.2.103 vrf v1 sou lo0
r5 tping 100 10 4321::103 vrf v1 sou lo0
r5 tping 100 10 2.2.2.104 vrf v1 sou lo0
r5 tping 100 10 4321::104 vrf v1 sou lo0
r5 tping 100 10 2.2.2.105 vrf v1 sou lo0
r5 tping 100 10 4321::105 vrf v1 sou lo0
r5 tping 100 10 2.2.2.106 vrf v1 sou lo0
r5 tping 100 10 4321::106 vrf v1 sou lo0

r6 tping 100 10 2.2.2.101 vrf v1 sou lo0
r6 tping 100 10 4321::101 vrf v1 sou lo0
r6 tping 100 10 2.2.2.103 vrf v1 sou lo0
r6 tping 100 10 4321::103 vrf v1 sou lo0
r6 tping 100 10 2.2.2.104 vrf v1 sou lo0
r6 tping 100 10 4321::104 vrf v1 sou lo0
r6 tping 100 10 2.2.2.105 vrf v1 sou lo0
r6 tping 100 10 4321::105 vrf v1 sou lo0
r6 tping 100 10 2.2.2.106 vrf v1 sou lo0
r6 tping 100 10 4321::106 vrf v1 sou lo0

r1 tping 100 30 2.2.2.201 vrf v1 sou lo1
r1 tping 100 30 4321::201 vrf v1 sou lo1
r1 tping 100 30 2.2.2.203 vrf v1 sou lo1
r1 tping 100 30 4321::203 vrf v1 sou lo1
r1 tping 100 30 2.2.2.204 vrf v1 sou lo1
r1 tping 100 30 4321::204 vrf v1 sou lo1
r1 tping 100 30 2.2.2.205 vrf v1 sou lo1
r1 tping 100 30 4321::205 vrf v1 sou lo1
r1 tping 100 30 2.2.2.206 vrf v1 sou lo1
r1 tping 100 30 4321::206 vrf v1 sou lo1

r3 tping 100 30 2.2.2.201 vrf v1 sou lo1
r3 tping 100 30 4321::201 vrf v1 sou lo1
r3 tping 100 30 2.2.2.203 vrf v1 sou lo1
r3 tping 100 30 4321::203 vrf v1 sou lo1
r3 tping 100 30 2.2.2.204 vrf v1 sou lo1
r3 tping 100 30 4321::204 vrf v1 sou lo1
r3 tping 100 30 2.2.2.205 vrf v1 sou lo1
r3 tping 100 30 4321::205 vrf v1 sou lo1
r3 tping 100 30 2.2.2.206 vrf v1 sou lo1
r3 tping 100 30 4321::206 vrf v1 sou lo1

r4 tping 100 30 2.2.2.201 vrf v1 sou lo1
r4 tping 100 30 4321::201 vrf v1 sou lo1
r4 tping 100 30 2.2.2.203 vrf v1 sou lo1
r4 tping 100 30 4321::203 vrf v1 sou lo1
r4 tping 100 30 2.2.2.204 vrf v1 sou lo1
r4 tping 100 30 4321::204 vrf v1 sou lo1
r4 tping 100 30 2.2.2.205 vrf v1 sou lo1
r4 tping 100 30 4321::205 vrf v1 sou lo1
r4 tping 100 30 2.2.2.206 vrf v1 sou lo1
r4 tping 100 30 4321::206 vrf v1 sou lo1

r5 tping 100 30 2.2.2.201 vrf v1 sou lo1
r5 tping 100 30 4321::201 vrf v1 sou lo1
r5 tping 100 30 2.2.2.203 vrf v1 sou lo1
r5 tping 100 30 4321::203 vrf v1 sou lo1
r5 tping 100 30 2.2.2.204 vrf v1 sou lo1
r5 tping 100 30 4321::204 vrf v1 sou lo1
r5 tping 100 30 2.2.2.205 vrf v1 sou lo1
r5 tping 100 30 4321::205 vrf v1 sou lo1
r5 tping 100 30 2.2.2.206 vrf v1 sou lo1
r5 tping 100 30 4321::206 vrf v1 sou lo1

r6 tping 100 30 2.2.2.201 vrf v1 sou lo1
r6 tping 100 30 4321::201 vrf v1 sou lo1
r6 tping 100 30 2.2.2.203 vrf v1 sou lo1
r6 tping 100 30 4321::203 vrf v1 sou lo1
r6 tping 100 30 2.2.2.204 vrf v1 sou lo1
r6 tping 100 30 4321::204 vrf v1 sou lo1
r6 tping 100 30 2.2.2.205 vrf v1 sou lo1
r6 tping 100 30 4321::205 vrf v1 sou lo1
r6 tping 100 30 2.2.2.206 vrf v1 sou lo1
r6 tping 100 30 4321::206 vrf v1 sou lo1

r1 dping sdn . r6 2.2.2.205 vrf v1 sou lo1
r1 dping sdn . r6 4321::205 vrf v1 sou lo1
