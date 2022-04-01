description unicast+flowspec over bgp

addrouter r1
int eth1 eth 0000.0000.1111 $1a$ $1b$
!
vrf def v1
 rd 1:1
 exit
int lo0
 vrf for v1
 ipv4 addr 2.2.2.1 255.255.255.255
 ipv6 addr 4321::1 ffff:ffff:ffff:ffff:ffff:ffff:ffff:ffff
 exit
int lo1
 vrf for v1
 ipv4 addr 2.2.2.101 255.255.255.255
 ipv6 addr 4321::101 ffff:ffff:ffff:ffff:ffff:ffff:ffff:ffff
 exit
int lo2
 vrf for v1
 ipv4 addr 2.2.2.201 255.255.255.255
 ipv6 addr 4321::201 ffff:ffff:ffff:ffff:ffff:ffff:ffff:ffff
 exit
int eth1
 vrf for v1
 ipv4 addr 1.1.1.1 255.255.255.252
 ipv6 addr 1234:1::1 ffff:ffff::
 exit
router uni2flow4 1
 vrf v1
 dist 10
 justadvert lo1
 exit
router uni2flow6 1
 vrf v1
 dist 10
 justadvert lo1
 exit
router bgp4 1
 vrf v1
 no safe-ebgp
 address uni flowspec
 local-as 1
 router-id 4.4.4.1
 neigh 1.1.1.2 remote-as 2
 neigh 1.1.1.2 send-comm both
 red conn
 red uni2flow4 1
 exit
router bgp6 1
 vrf v1
 no safe-ebgp
 address uni flowspec
 local-as 1
 router-id 6.6.6.1
 neigh 1234:1::2 remote-as 2
 neigh 1234:1::2 send-comm both
 red conn
 red uni2flow6 1
 exit
!

addrouter r2
int eth1 eth 0000.0000.2222 $1b$ $1a$
!
vrf def v1
 rd 1:1
 exit
int lo0
 vrf for v1
 ipv4 addr 2.2.2.2 255.255.255.255
 ipv6 addr 4321::2 ffff:ffff:ffff:ffff:ffff:ffff:ffff:ffff
 exit
int eth1
 vrf for v1
 ipv4 addr 1.1.1.2 255.255.255.252
 ipv6 addr 1234:1::2 ffff:ffff::
 exit
router bgp4 1
 vrf v1
 no safe-ebgp
 address uni flowspec
 local-as 2
 router-id 4.4.4.2
 neigh 1.1.1.1 remote-as 1
 neigh 1.1.1.1 send-comm both
 red conn
 flowspec-install
 exit
router bgp6 1
 vrf v1
 no safe-ebgp
 address uni flowspec
 local-as 2
 router-id 6.6.6.2
 neigh 1234:1::1 remote-as 1
 neigh 1234:1::1 send-comm both
 red conn
 flowspec-install
 exit
!





r1 tping 100 60 2.2.2.2 /vrf v1 /int lo0
r1 tping 100 60 4321::2 /vrf v1 /int lo0

r2 tping 100 60 2.2.2.1 /vrf v1 /int lo0
r2 tping 100 60 4321::1 /vrf v1 /int lo0

r2 tping 0 60 2.2.2.101 /vrf v1 /int lo0
r2 tping 0 60 4321::101 /vrf v1 /int lo0

r2 tping 100 60 2.2.2.201 /vrf v1 /int lo0
r2 tping 100 60 4321::201 /vrf v1 /int lo0
