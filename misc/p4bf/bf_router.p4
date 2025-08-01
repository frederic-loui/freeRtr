/*
 * Copyright 2019-present GEANT RARE project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed On an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */



#include <core.p4>

#if __TARGET_TOFINO__ == 2
#include <t2na.p4>
#else
#include <tna.p4>
#endif

#include "rare_profiles.p4"
#include "include/cst_table_size.p4"
#include "include/cst_bundle.p4"
#include "include/def_types.p4"
#include "include/cst_cpu_port.p4"
#include "include/cst_needs.p4"
#include "include/cst_ethertype.p4"
#include "include/cst_ip_protocol.p4"
#include "include/hdr_cpu.p4"
#include "include/hdr_bier.p4"
#include "include/hdr_ethernet.p4"
#include "include/hdr_sgt.p4"
#include "include/hdr_arp.p4"
#include "include/hdr_llc.p4"
#include "include/hdr_vlan.p4"
#include "include/hdr_mpls.p4"
#include "include/hdr_polka.p4"
#include "include/hdr_nsh.p4"
#include "include/hdr_ipv4.p4"
#include "include/hdr_ipv6.p4"
#include "include/hdr_tcp.p4"
#include "include/hdr_udp.p4"
#include "include/hdr_gre.p4"
#include "include/hdr_tmux.p4"
#include "include/hdr_pppoe.p4"
#include "include/hdr_l2tp.p4"
#include "include/hdr_l3tp.p4"
#include "include/hdr_vxlan.p4"
#include "include/hdr_etherip.p4"
#include "include/hdr_gtp.p4"

/*----------------------------------------------------------------------------*
 *                   I N G R E S S   P R O C E S S I N G                      *
 *----------------------------------------------------------------------------*/

/*------------------ I N G R E S S  G L O B A L  M E T A D A T A ------------ */
#include "include/mtd_bridged_metadata.p4"
#include "include/mtd_port_metadata.p4"
#include "include/mtd_ig_metadata.p4"

/*------------------ I N G R E S S  H E A D E R S --------------------------- */
#include "include/hdr_ig_headers.p4"

/*------------------ I N G R E S S   P A R S E R -----------------------------*/
#include "include/ig_prs_main.p4"

/*------------------ I N G R E S S - M A T C H - A C T I O N ---------------- */
#include "include/hsh_ipv4_ipv6_hash.p4"

#include "include/ig_ctl_bundle.p4"
#include "include/ig_ctl_pkt_pre_emit.p4"
#include "include/ig_ctl_vlan_in.p4"
#include "include/ig_ctl_sgt.p4"
#include "include/ig_ctl_frag.p4"
#include "include/ig_ctl_acl_in.p4"
#include "include/ig_ctl_acl_out.p4"
#include "include/ig_ctl_vrf.p4"
#include "include/ig_ctl_bridge.p4"
#include "include/ig_ctl_mpls.p4"
#include "include/ig_ctl_polka.p4"
#include "include/ig_ctl_nsh.p4"
#include "include/ig_ctl_ipv4.p4"
#include "include/ig_ctl_ipv6.p4"
#include "include/ig_ctl_ipv4b.p4"
#include "include/ig_ctl_ipv6b.p4"
#include "include/ig_ctl_nat.p4"
#include "include/ig_ctl_pbr.p4"
#include "include/ig_ctl_qos_in.p4"
#include "include/ig_ctl_qos_out.p4"
#include "include/ig_ctl_rate_in.p4"
#include "include/ig_ctl_rate_out.p4"
#include "include/ig_ctl_mcast.p4"
#include "include/ig_ctl_flowspec.p4"
#include "include/ig_ctl_tunnel.p4"
#include "include/ig_ctl_pppoe.p4"
#include "include/ig_ctl_copp.p4"
#include "include/ig_ctl_outport.p4"
#include "include/ig_ctl_rewrites.p4"
#include "include/ig_ctl.p4"

/*------------------ I N G R E S S  D E P A R S E R ------------------------- */
#include "include/ig_ctl_dprs.p4"

/*----------------------------------------------------------------------------*
 *                   E G R E S S   P R O C E S S I N G                        *
 *----------------------------------------------------------------------------*/

/*------------------ E G R E S S  H E A D E R S ----------------------------- */

/*------------------ E G R E S S  G L O B A L  M E T A D A T A -------------- */

/*------------------ E G R E S S  P A R S E R ------------------------------- */
#include "include/eg_prs_main.p4"

/*------------------ E G R E S S  M A T C H - A C T I O N ------------------- */
#include "include/eg_ctl_nexthop.p4"
#include "include/eg_ctl_mcast.p4"
#include "include/eg_ctl_outport.p4"
#include "include/eg_ctl_vlan_out.p4"
#include "include/eg_ctl_sgt.p4"
#include "include/eg_ctl_hairpin.p4"
#include "include/eg_ctl.p4"

/*------------------ E G R E S S  D E P A R S E R --------------------------- */
#include "include/eg_ctl_dprs.p4"

/*------------------ F I N A L  P A C K A G E ------------------------------- */

Pipeline(
    ig_prs_main(),
    ig_ctl(),
    ig_ctl_dprs(),
    eg_prs_main(),
    eg_ctl(),
    eg_ctl_dprs()
) pipe;

Switch(pipe) main;

