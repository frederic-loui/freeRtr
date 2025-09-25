description unicast+l3evpns over ibgp with dynamic capability

addrouter r1
int eth1 eth 0000.0000.1111 $1a$ $1b$
!
vrf def v1
 rd 1:1
 label-mode per-prefix
 exit
vrf def v2
 rd 1:2
 rt-both 1:2
 exit
vrf def v3
 rd 1:3
 rt-both 1:3
 exit
vrf def v4
 rd 1:4
 rt-both 1:4
 exit
int lo0
 vrf for v1
 ipv4 addr 2.2.2.1 255.255.255.255
 ipv6 addr 4321::1 ffff:ffff:ffff:ffff:ffff:ffff:ffff:ffff
 exit
int lo1
 vrf for v1
 ipv4 addr 2.2.2.11 255.255.255.255
 ipv6 addr 4321::11 ffff:ffff:ffff:ffff:ffff:ffff:ffff:ffff
 exit
int lo2
 vrf for v2
 ipv4 addr 9.9.2.1 255.255.255.255
 ipv6 addr 9992::1 ffff:ffff:ffff:ffff:ffff:ffff:ffff:ffff
 exit
int lo3
 vrf for v3
 ipv4 addr 9.9.3.1 255.255.255.255
 ipv6 addr 9993::1 ffff:ffff:ffff:ffff:ffff:ffff:ffff:ffff
 exit
int lo4
 vrf for v4
 ipv4 addr 9.9.4.1 255.255.255.255
 ipv6 addr 9994::1 ffff:ffff:ffff:ffff:ffff:ffff:ffff:ffff
 exit
int eth1
 vrf for v1
 ipv4 addr 1.1.1.1 255.255.255.252
 ipv6 addr 1234:1::1 ffff:ffff::
 mpls enable
 mpls ldp4
 mpls ldp6
 exit
ipv4 route v1 2.2.2.2 255.255.255.255 1.1.1.2
ipv6 route v1 4321::2 ffff:ffff:ffff:ffff:ffff:ffff:ffff:ffff 1234:1::2
router bgp4 1
 vrf v1
 no safe-ebgp
 address unicast evpn
 local-as 1
 router-id 4.4.4.1
 neigh 2.2.2.2 remote-as 1
 neigh 2.2.2.2 update lo0
 neigh 2.2.2.2 send-comm both
 neigh 2.2.2.2 dynamic
 afi-l3e v2 ena
 afi-l3e v2 red conn
 afi-l3e v3 ena
 afi-l3e v3 red conn
 afi-l3e v4 ena
 afi-l3e v4 red conn
 red conn
 exit
router bgp6 1
 vrf v1
 no safe-ebgp
 address unicast evpn
 local-as 1
 router-id 6.6.6.1
 neigh 4321::2 remote-as 1
 neigh 4321::2 update lo0
 neigh 4321::2 send-comm both
 neigh 4321::2 dynamic
 afi-l3e v2 ena
 afi-l3e v2 red conn
 afi-l3e v3 ena
 afi-l3e v3 red conn
 afi-l3e v4 ena
 afi-l3e v4 red conn
 red conn
 exit
!

addrouter r2
int eth1 eth 0000.0000.2222 $1b$ $1a$
!
vrf def v1
 rd 1:1
 label-mode per-prefix
 exit
vrf def v2
 rd 1:2
 rt-both 1:2
 exit
vrf def v3
 rd 1:3
 rt-both 1:3
 exit
vrf def v4
 rd 1:4
 rt-both 1:4
 exit
int lo0
 vrf for v1
 ipv4 addr 2.2.2.2 255.255.255.255
 ipv6 addr 4321::2 ffff:ffff:ffff:ffff:ffff:ffff:ffff:ffff
 exit
int lo1
 vrf for v1
 ipv4 addr 2.2.2.12 255.255.255.255
 ipv6 addr 4321::12 ffff:ffff:ffff:ffff:ffff:ffff:ffff:ffff
 exit
int lo2
 vrf for v2
 ipv4 addr 9.9.2.2 255.255.255.255
 ipv6 addr 9992::2 ffff:ffff:ffff:ffff:ffff:ffff:ffff:ffff
 exit
int lo3
 vrf for v3
 ipv4 addr 9.9.3.2 255.255.255.255
 ipv6 addr 9993::2 ffff:ffff:ffff:ffff:ffff:ffff:ffff:ffff
 exit
int lo4
 vrf for v4
 ipv4 addr 9.9.4.2 255.255.255.255
 ipv6 addr 9994::2 ffff:ffff:ffff:ffff:ffff:ffff:ffff:ffff
 exit
int eth1
 vrf for v1
 ipv4 addr 1.1.1.2 255.255.255.252
 ipv6 addr 1234:1::2 ffff:ffff::
 mpls enable
 mpls ldp4
 mpls ldp6
 exit
ipv4 route v1 2.2.2.1 255.255.255.255 1.1.1.1
ipv6 route v1 4321::1 ffff:ffff:ffff:ffff:ffff:ffff:ffff:ffff 1234:1::1
router bgp4 1
 vrf v1
 no safe-ebgp
 address unicast evpn
 local-as 1
 router-id 4.4.4.2
 neigh 2.2.2.1 remote-as 1
 neigh 2.2.2.1 update lo0
 neigh 2.2.2.1 send-comm both
 neigh 2.2.2.1 dynamic
 afi-l3e v2 ena
 afi-l3e v2 red conn
 afi-l3e v3 ena
 afi-l3e v3 red conn
 afi-l3e v4 ena
 afi-l3e v4 red conn
 red conn
 exit
router bgp6 1
 vrf v1
 no safe-ebgp
 address unicast evpn
 local-as 1
 router-id 6.6.6.2
 neigh 4321::1 remote-as 1
 neigh 4321::1 update lo0
 neigh 4321::1 send-comm both
 neigh 4321::1 dynamic
 afi-l3e v2 ena
 afi-l3e v2 red conn
 afi-l3e v3 ena
 afi-l3e v3 red conn
 afi-l3e v4 ena
 afi-l3e v4 red conn
 red conn
 exit
!





r1 tping 100 60 2.2.2.2 vrf v1 sou lo0
r1 tping 100 60 4321::2 vrf v1 sou lo0

r2 tping 100 60 2.2.2.1 vrf v1 sou lo0
r2 tping 100 60 4321::1 vrf v1 sou lo0

r1 tping 100 60 2.2.2.12 vrf v1 sou lo1
r1 tping 100 60 4321::12 vrf v1 sou lo1

r2 tping 100 60 2.2.2.11 vrf v1 sou lo1
r2 tping 100 60 4321::11 vrf v1 sou lo1

r1 tping 100 60 9.9.2.2 vrf v2
r2 tping 100 60 9.9.2.1 vrf v2
r1 tping 100 60 9992::2 vrf v2
r2 tping 100 60 9992::1 vrf v2

r1 tping 100 60 9.9.3.2 vrf v3
r2 tping 100 60 9.9.3.1 vrf v3
r1 tping 100 60 9993::2 vrf v3
r2 tping 100 60 9993::1 vrf v3

r1 tping 100 60 9.9.4.2 vrf v4
r2 tping 100 60 9.9.4.1 vrf v4
r1 tping 100 60 9994::2 vrf v4
r2 tping 100 60 9994::1 vrf v4

r1 send clear ipv4 bgp 1 peer 2.2.2.2 del evpn
r1 send clear ipv6 bgp 1 peer 4321::2 del evpn

r1 tping 100 5 2.2.2.12 vrf v1 sou lo1
r1 tping 100 5 4321::12 vrf v1 sou lo1

r2 tping 100 5 2.2.2.11 vrf v1 sou lo1
r2 tping 100 5 4321::11 vrf v1 sou lo1

r1 tping 0 5 9.9.2.2 vrf v2
r2 tping 0 5 9.9.2.1 vrf v2
r1 tping 0 5 9992::2 vrf v2
r2 tping 0 5 9992::1 vrf v2

r1 tping 0 5 9.9.3.2 vrf v3
r2 tping 0 5 9.9.3.1 vrf v3
r1 tping 0 5 9993::2 vrf v3
r2 tping 0 5 9993::1 vrf v3

r1 tping 0 5 9.9.4.2 vrf v4
r2 tping 0 5 9.9.4.1 vrf v4
r1 tping 0 5 9994::2 vrf v4
r2 tping 0 5 9994::1 vrf v4

r1 send clear ipv4 bgp 1 peer 2.2.2.2 add evpn
r1 send clear ipv6 bgp 1 peer 4321::2 add evpn

r1 tping 100 5 2.2.2.12 vrf v1 sou lo1
r1 tping 100 5 4321::12 vrf v1 sou lo1

r2 tping 100 5 2.2.2.11 vrf v1 sou lo1
r2 tping 100 5 4321::11 vrf v1 sou lo1

r1 tping 100 5 9.9.2.2 vrf v2
r2 tping 100 5 9.9.2.1 vrf v2
r1 tping 100 5 9992::2 vrf v2
r2 tping 100 5 9992::1 vrf v2

r1 tping 100 5 9.9.3.2 vrf v3
r2 tping 100 5 9.9.3.1 vrf v3
r1 tping 100 5 9993::2 vrf v3
r2 tping 100 5 9993::1 vrf v3

r1 tping 100 5 9.9.4.2 vrf v4
r2 tping 100 5 9.9.4.1 vrf v4
r1 tping 100 5 9994::2 vrf v4
r2 tping 100 5 9994::1 vrf v4
