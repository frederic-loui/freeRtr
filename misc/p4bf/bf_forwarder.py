#!/usr/bin/env python3

###############################################################################
#
# Copyright 2019-present GEANT RARE project
#
# Licensed under the Apache License, Version 2.0 (the "License");
# you may not use this file except in compliance with the License.
# You may obtain a copy of the License at
#
#     http://www.apache.org/licenses/LICENSE-2.0
#
# Unless required by applicable law or agreed to in writing, software
# distributed under the License is distributed On an "AS IS" BASIS,
# WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
# See the License for the specific language governing permissions and
# limitations under the License.
#
###############################################################################

from rare.bf_gbl_env.cst_env import *
from rare.bf_grpc_client import BfRuntimeGrpcClient
from rare.bf_ifstatus import BfIfStatus
from rare.bf_ifcounter import BfIfCounter
from rare.bf_subifcounter import BfSubIfCounter
from rare.bf_natcounter import BfNatCounter
from rare.bf_bridgecounter import BfBridgeCounter
from rare.bf_snmp_client import BfIfSnmpClient
from rare.bf_forwarder import BfForwarder
from rare.bf_forwarder.opt_parser import get_opt_parser

try:
    from salgrpcclient import SalGrpcClient
except ImportError:
    logger.warning("SAL import failed")

ALL_THREADS = []

if __name__ == "__main__":

    args = get_opt_parser()

    try:

        if args.platform == "stordis_bf2556x_1t":
            # start TOFINO via SAL GRPC client
            logger.warning("Starting TOFINO via SAL")
            sal_client = SalGrpcClient(args.sal_grpc_server_address)
            sal_client.TestConnection()
            sal_client.GetSwitchModel()
            sal_client.StartTofino()
            sal_client.StartGearBox()
        else:
            sal_client = None

        logger.warning("%s running on: %s" % (PROGRAM_NAME, args.platform.upper()))

        sck = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
        while True:
            try:
                sck.connect((args.freerouter_address, args.freerouter_port))
            except socket.error as e:
                logger.error(
                    "Failed to connect to control plane process: %s, retrying" % e
                )
                sleep(2)
                continue
            else:
                logger.warning(
                    "Connected to control plane %s:%s"
                    % (args.freerouter_address, args.freerouter_port)
                )
                break

        sckr_file = sck.makefile("r")
        sckw_file = sck.makefile("w")

        bf_client = BfRuntimeGrpcClient(
            args.bfruntime_address,
            args.p4_program_name,
            args.client_id,
            args.pipe_name,
            True,
        )

        bf_ifstatus_c = BfRuntimeGrpcClient(
            args.bfruntime_address,
            args.p4_program_name,
            args.client_id + 1,
            args.pipe_name,
            False,
        )

        bf_ifcounter_c = BfRuntimeGrpcClient(
            args.bfruntime_address,
            args.p4_program_name,
            args.client_id + 2,
            args.pipe_name,
            False,
        )

        bf_subifcounter_c = BfRuntimeGrpcClient(
            args.bfruntime_address,
            args.p4_program_name,
            args.client_id + 3,
            args.pipe_name,
            False,
        )

        bf_natcounter_c = BfRuntimeGrpcClient(
            args.bfruntime_address,
            args.p4_program_name,
            args.client_id + 4,
            args.pipe_name,
            False,
        )

        bf_bridgecounter_c = BfRuntimeGrpcClient(
            args.bfruntime_address,
            args.p4_program_name,
            args.client_id + 5,
            args.pipe_name,
            False,
        )

        bf_forwarder = BfForwarder(
            1,
            "bf_forwarder",
            args.platform,
            bf_client,
            sal_client,
            sckr_file,
            sckw_file,
            args.no_log_keepalive,
        )

        bf_forwarder.daemon = True
        bf_forwarder.start()
        ALL_THREADS.append(bf_forwarder)

        bf_ifstatus = BfIfStatus(2, "bf_ifstatus", bf_ifstatus_c, sckw_file, 1)

        bf_ifstatus.daemon = True
        bf_ifstatus.start()
        ALL_THREADS.append(bf_ifstatus)

        bf_ifcounter = BfIfCounter(
            3, "bf_ifcounter", bf_ifcounter_c, sckw_file, args.pipe_name, 5
        )

        bf_ifcounter.daemon = True
        bf_ifcounter.start()
        ALL_THREADS.append(bf_ifcounter)

        bf_subifcounter = BfSubIfCounter(
            5, "bf_subifcounter", bf_subifcounter_c, sckw_file, args.pipe_name, 5
        )

        bf_subifcounter.daemon = True
        bf_subifcounter.start()
        ALL_THREADS.append(bf_subifcounter)

        if bf_forwarder.dp_capabilities["nat"] == True:
            bf_natcounter = BfNatCounter(
                5, "bf_natcounter", bf_natcounter_c, sckw_file, args.pipe_name, 30
            )
            bf_natcounter.daemon = True
            bf_natcounter.start()
            ALL_THREADS.append(bf_natcounter)
        else:
            logging.warning("%s - nat not supported" % PROGRAM_NAME)

        if bf_forwarder.dp_capabilities["bridge"] == True:
            bf_bridgecounter = BfBridgeCounter(
                6, "bf_bridgecounter", bf_bridgecounter_c, sckw_file, args.pipe_name, 30
            )
            bf_bridgecounter.daemon = True
            bf_bridgecounter.start()
            ALL_THREADS.append(bf_bridgecounter)
        else:
            logging.warning("%s - bridge not supported" % PROGRAM_NAME)

        if args.snmp:
            bf_snmp = BfIfSnmpClient(
                7,
                "bf_snmp",
                bf_client,
                args.ifmibs_dir,
                args.stats_interval,
                args.ifindex,
            )
            bf_snmp.daemon = True
            bf_snmp.start()
            ALL_THREADS.append(bf_snmp)
            logger.warning("bf_switchd started with SNMP export")
        else:
            logger.warning("bf_switchd started with no SNMP export")

        while is_any_thread_alive(ALL_THREADS):
            [t.join(1) for t in ALL_THREADS if t is not None and t.is_alive()]

    except KeyboardInterrupt:
        logger.warning("\n%s - Received signal 2 ..." % PROGRAM_NAME)
        for t in ALL_THREADS:
            t.die = True
        logger.warning("%s - Quitting !" % PROGRAM_NAME)
        graceful_exit(bf_client, sck)
