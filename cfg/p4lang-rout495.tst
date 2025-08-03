description p4lang: ouni with bgp over vlan

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
 exit
int lo1
 vrf for v1
 ipv4 addr 3.3.3.101 255.255.255.255
 ipv6 addr 3333::101 ffff:ffff:ffff:ffff:ffff:ffff:ffff:ffff
 exit
int sdn1
 exit
int sdn1.111
 vrf for v1
 ipv4 addr 1.1.1.1 255.255.255.0
 ipv6 addr 1234:1::1 ffff:ffff::
 ipv6 ena
 exit
int sdn2
 exit
int sdn2.111
 vrf for v1
 ipv4 addr 1.1.2.1 255.255.255.0
 ipv6 addr 1234:2::1 ffff:ffff::
 ipv6 ena
 exit
int sdn3
 exit
int sdn3.111
 vrf for v1
 ipv4 addr 1.1.3.1 255.255.255.0
 ipv6 addr 1234:3::1 ffff:ffff::
 ipv6 ena
 exit
int sdn4
 exit
int sdn4.111
 vrf for v1
 ipv4 addr 1.1.4.1 255.255.255.0
 ipv6 addr 1234:4::1 ffff:ffff::
 ipv6 ena
 exit
router bgp4 1
 vrf v1
 address ouni
 local-as 1
 router-id 4.4.4.1
 temp a remote-as 1
 temp a update lo0
 temp a route-reflect
 neigh 2.2.2.103 temp a
 neigh 2.2.2.104 temp a
 neigh 2.2.2.105 temp a
 neigh 2.2.2.106 temp a
 afi-other ena
 no afi-other vpn
 afi-other red conn
 afi-other red stat
 exit
router bgp6 1
 vrf v1
 address ouni
 local-as 1
 router-id 6.6.6.1
 temp a remote-as 1
 temp a update lo0
 temp a route-reflect
 neigh 4321::103 temp a
 neigh 4321::104 temp a
 neigh 4321::105 temp a
 neigh 4321::106 temp a
 afi-other ena
 no afi-other vpn
 afi-other red conn
 afi-other red stat
 exit
server p4lang p4
 interconnect eth2
 export-vrf v1
 export-port sdn1 1 10
 export-port sdn2 2 10
 export-port sdn3 3 10
 export-port sdn4 4 10
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
!

addother r2 controller r1 v9 9080 - feature route vlan
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
int lo0
 vrf for v1
 ipv4 addr 2.2.2.103 255.255.255.255
 ipv6 addr 4321::103 ffff:ffff:ffff:ffff:ffff:ffff:ffff:ffff
 exit
int lo1
 vrf for v1
 ipv4 addr 3.3.3.103 255.255.255.255
 ipv6 addr 3333::103 ffff:ffff:ffff:ffff:ffff:ffff:ffff:ffff
 exit
int eth1
 exit
int eth1.111
 vrf for v1
 ipv4 addr 1.1.1.2 255.255.255.0
 ipv6 addr 1234:1::2 ffff:ffff::
 exit
router bgp4 1
 vrf v1
 address ouni
 local-as 1
 router-id 4.4.4.3
 neigh 2.2.2.101 remote-as 1
 neigh 2.2.2.101 update lo0
 afi-other ena
 no afi-other vpn
 afi-other red conn
 afi-other red stat
 exit
router bgp6 1
 vrf v1
 address ouni
 local-as 1
 router-id 6.6.6.3
 neigh 4321::101 remote-as 1
 neigh 4321::101 update lo0
 afi-other ena
 no afi-other vpn
 afi-other red conn
 afi-other red stat
 exit
ipv4 route v1 1.1.2.0 255.255.255.0 1.1.1.1
ipv6 route v1 1234:2:: ffff:ffff:: 1234:1::1
ipv4 route v1 2.2.2.101 255.255.255.255 1.1.1.1
ipv4 route v1 2.2.2.104 255.255.255.255 1.1.1.1
ipv4 route v1 2.2.2.105 255.255.255.255 1.1.1.1
ipv4 route v1 2.2.2.106 255.255.255.255 1.1.1.1
ipv6 route v1 4321::101 ffff:ffff:ffff:ffff:ffff:ffff:ffff:ffff 1234:1::1
ipv6 route v1 4321::104 ffff:ffff:ffff:ffff:ffff:ffff:ffff:ffff 1234:1::1
ipv6 route v1 4321::105 ffff:ffff:ffff:ffff:ffff:ffff:ffff:ffff 1234:1::1
ipv6 route v1 4321::106 ffff:ffff:ffff:ffff:ffff:ffff:ffff:ffff 1234:1::1
!

addrouter r4
int eth1 eth 0000.0000.4444 $4b$ $4a$
!
vrf def v1
 rd 1:1
 label-mode per-prefix
 exit
int lo0
 vrf for v1
 ipv4 addr 2.2.2.104 255.255.255.255
 ipv6 addr 4321::104 ffff:ffff:ffff:ffff:ffff:ffff:ffff:ffff
 exit
int lo1
 vrf for v1
 ipv4 addr 3.3.3.104 255.255.255.255
 ipv6 addr 3333::104 ffff:ffff:ffff:ffff:ffff:ffff:ffff:ffff
 exit
int eth1
 exit
int eth1.111
 vrf for v1
 ipv4 addr 1.1.2.2 255.255.255.0
 ipv6 addr 1234:2::2 ffff:ffff::
 exit
router bgp4 1
 vrf v1
 address ouni
 local-as 1
 router-id 4.4.4.4
 neigh 2.2.2.101 remote-as 1
 neigh 2.2.2.101 update lo0
 afi-other ena
 no afi-other vpn
 afi-other red conn
 afi-other red stat
 exit
router bgp6 1
 vrf v1
 address ouni
 local-as 1
 router-id 6.6.6.4
 neigh 4321::101 remote-as 1
 neigh 4321::101 update lo0
 afi-other ena
 no afi-other vpn
 afi-other red conn
 afi-other red stat
 exit
ipv4 route v1 1.1.1.0 255.255.255.0 1.1.2.1
ipv6 route v1 1234:1:: ffff:ffff:: 1234:2::1
ipv4 route v1 2.2.2.101 255.255.255.255 1.1.2.1
ipv4 route v1 2.2.2.103 255.255.255.255 1.1.2.1
ipv4 route v1 2.2.2.105 255.255.255.255 1.1.2.1
ipv4 route v1 2.2.2.106 255.255.255.255 1.1.2.1
ipv6 route v1 4321::101 ffff:ffff:ffff:ffff:ffff:ffff:ffff:ffff 1234:2::1
ipv6 route v1 4321::103 ffff:ffff:ffff:ffff:ffff:ffff:ffff:ffff 1234:2::1
ipv6 route v1 4321::105 ffff:ffff:ffff:ffff:ffff:ffff:ffff:ffff 1234:2::1
ipv6 route v1 4321::106 ffff:ffff:ffff:ffff:ffff:ffff:ffff:ffff 1234:2::1
!

addrouter r5
int eth1 eth 0000.0000.5555 $5b$ $5a$
!
vrf def v1
 rd 1:1
 label-mode per-prefix
 exit
int lo0
 vrf for v1
 ipv4 addr 2.2.2.105 255.255.255.255
 ipv6 addr 4321::105 ffff:ffff:ffff:ffff:ffff:ffff:ffff:ffff
 exit
int lo1
 vrf for v1
 ipv4 addr 3.3.3.105 255.255.255.255
 ipv6 addr 3333::105 ffff:ffff:ffff:ffff:ffff:ffff:ffff:ffff
 exit
int eth1
 exit
int eth1.111
 vrf for v1
 ipv4 addr 1.1.3.2 255.255.255.0
 ipv6 addr 1234:3::2 ffff:ffff::
 exit
router bgp4 1
 vrf v1
 address ouni
 local-as 1
 router-id 4.4.4.5
 neigh 2.2.2.101 remote-as 1
 neigh 2.2.2.101 update lo0
 afi-other ena
 no afi-other vpn
 afi-other red conn
 afi-other red stat
 exit
router bgp6 1
 vrf v1
 address ouni
 local-as 1
 router-id 6.6.6.5
 neigh 4321::101 remote-as 1
 neigh 4321::101 update lo0
 afi-other ena
 no afi-other vpn
 afi-other red conn
 afi-other red stat
 exit
ipv4 route v1 1.1.4.0 255.255.255.0 1.1.3.1
ipv6 route v1 1234:4:: ffff:ffff:: 1234:3::1
ipv4 route v1 2.2.2.101 255.255.255.255 1.1.3.1
ipv4 route v1 2.2.2.103 255.255.255.255 1.1.3.1
ipv4 route v1 2.2.2.104 255.255.255.255 1.1.3.1
ipv4 route v1 2.2.2.106 255.255.255.255 1.1.3.1
ipv6 route v1 4321::101 ffff:ffff:ffff:ffff:ffff:ffff:ffff:ffff 1234:3::1
ipv6 route v1 4321::103 ffff:ffff:ffff:ffff:ffff:ffff:ffff:ffff 1234:3::1
ipv6 route v1 4321::104 ffff:ffff:ffff:ffff:ffff:ffff:ffff:ffff 1234:3::1
ipv6 route v1 4321::106 ffff:ffff:ffff:ffff:ffff:ffff:ffff:ffff 1234:3::1
!

addrouter r6
int eth1 eth 0000.0000.6666 $6b$ $6a$
!
vrf def v1
 rd 1:1
 label-mode per-prefix
 exit
int lo0
 vrf for v1
 ipv4 addr 2.2.2.106 255.255.255.255
 ipv6 addr 4321::106 ffff:ffff:ffff:ffff:ffff:ffff:ffff:ffff
 exit
int lo1
 vrf for v1
 ipv4 addr 3.3.3.106 255.255.255.255
 ipv6 addr 3333::106 ffff:ffff:ffff:ffff:ffff:ffff:ffff:ffff
 exit
int eth1
 exit
int eth1.111
 vrf for v1
 ipv4 addr 1.1.4.2 255.255.255.0
 ipv6 addr 1234:4::2 ffff:ffff::
 exit
router bgp4 1
 vrf v1
 address ouni
 local-as 1
 router-id 4.4.4.6
 neigh 2.2.2.101 remote-as 1
 neigh 2.2.2.101 update lo0
 afi-other ena
 no afi-other vpn
 afi-other red conn
 afi-other red stat
 exit
router bgp6 1
 vrf v1
 address ouni
 local-as 1
 router-id 6.6.6.6
 neigh 4321::101 remote-as 1
 neigh 4321::101 update lo0
 afi-other ena
 no afi-other vpn
 afi-other red conn
 afi-other red stat
 exit
ipv4 route v1 1.1.3.0 255.255.255.0 1.1.4.1
ipv6 route v1 1234:3:: ffff:ffff:: 1234:4::1
ipv4 route v1 2.2.2.101 255.255.255.255 1.1.4.1
ipv4 route v1 2.2.2.103 255.255.255.255 1.1.4.1
ipv4 route v1 2.2.2.104 255.255.255.255 1.1.4.1
ipv4 route v1 2.2.2.105 255.255.255.255 1.1.4.1
ipv6 route v1 4321::101 ffff:ffff:ffff:ffff:ffff:ffff:ffff:ffff 1234:4::1
ipv6 route v1 4321::103 ffff:ffff:ffff:ffff:ffff:ffff:ffff:ffff 1234:4::1
ipv6 route v1 4321::104 ffff:ffff:ffff:ffff:ffff:ffff:ffff:ffff 1234:4::1
ipv6 route v1 4321::105 ffff:ffff:ffff:ffff:ffff:ffff:ffff:ffff 1234:4::1
!


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

r1 tping 100 10 3.3.3.101 vrf v1 sou lo1
r1 tping 100 10 3333::101 vrf v1 sou lo1
r1 tping 100 10 3.3.3.103 vrf v1 sou lo1
r1 tping 100 10 3333::103 vrf v1 sou lo1
r1 tping 100 10 3.3.3.104 vrf v1 sou lo1
r1 tping 100 10 3333::104 vrf v1 sou lo1
r1 tping 100 10 3.3.3.105 vrf v1 sou lo1
r1 tping 100 10 3333::105 vrf v1 sou lo1
r1 tping 100 10 3.3.3.106 vrf v1 sou lo1
r1 tping 100 10 3333::106 vrf v1 sou lo1

r3 tping 100 10 3.3.3.101 vrf v1 sou lo1
r3 tping 100 10 3333::101 vrf v1 sou lo1
r3 tping 100 10 3.3.3.103 vrf v1 sou lo1
r3 tping 100 10 3333::103 vrf v1 sou lo1
r3 tping 100 10 3.3.3.104 vrf v1 sou lo1
r3 tping 100 10 3333::104 vrf v1 sou lo1
r3 tping 100 10 3.3.3.105 vrf v1 sou lo1
r3 tping 100 10 3333::105 vrf v1 sou lo1
r3 tping 100 10 3.3.3.106 vrf v1 sou lo1
r3 tping 100 10 3333::106 vrf v1 sou lo1

r4 tping 100 10 3.3.3.101 vrf v1 sou lo1
r4 tping 100 10 3333::101 vrf v1 sou lo1
r4 tping 100 10 3.3.3.103 vrf v1 sou lo1
r4 tping 100 10 3333::103 vrf v1 sou lo1
r4 tping 100 10 3.3.3.104 vrf v1 sou lo1
r4 tping 100 10 3333::104 vrf v1 sou lo1
r4 tping 100 10 3.3.3.105 vrf v1 sou lo1
r4 tping 100 10 3333::105 vrf v1 sou lo1
r4 tping 100 10 3.3.3.106 vrf v1 sou lo1
r4 tping 100 10 3333::106 vrf v1 sou lo1

r5 tping 100 10 3.3.3.101 vrf v1 sou lo1
r5 tping 100 10 3333::101 vrf v1 sou lo1
r5 tping 100 10 3.3.3.103 vrf v1 sou lo1
r5 tping 100 10 3333::103 vrf v1 sou lo1
r5 tping 100 10 3.3.3.104 vrf v1 sou lo1
r5 tping 100 10 3333::104 vrf v1 sou lo1
r5 tping 100 10 3.3.3.105 vrf v1 sou lo1
r5 tping 100 10 3333::105 vrf v1 sou lo1
r5 tping 100 10 3.3.3.106 vrf v1 sou lo1
r5 tping 100 10 3333::106 vrf v1 sou lo1

r6 tping 100 10 3.3.3.101 vrf v1 sou lo1
r6 tping 100 10 3333::101 vrf v1 sou lo1
r6 tping 100 10 3.3.3.103 vrf v1 sou lo1
r6 tping 100 10 3333::103 vrf v1 sou lo1
r6 tping 100 10 3.3.3.104 vrf v1 sou lo1
r6 tping 100 10 3333::104 vrf v1 sou lo1
r6 tping 100 10 3.3.3.105 vrf v1 sou lo1
r6 tping 100 10 3333::105 vrf v1 sou lo1
r6 tping 100 10 3.3.3.106 vrf v1 sou lo1
r6 tping 100 10 3333::106 vrf v1 sou lo1


r1 dping sdn . r4 3.3.3.105 vrf v1 sou lo1
r1 dping sdn . r4 3333::105 vrf v1 sou lo1
