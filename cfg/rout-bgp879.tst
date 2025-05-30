description bgp routemap filtering with aspa validity with soft-reconfig

addrouter r1
int eth1 eth 0000.0000.1111 $1a$ $1b$
int eth2 eth 0000.0000.2222 $2a$ $2b$
!
vrf def v1
 rd 1:1
 exit
bridge 1
 mac-learn
 exit
int lo0
 vrf for v1
 ipv4 addr 2.2.2.1 255.255.255.255
 ipv6 addr 4321::1 ffff:ffff:ffff:ffff:ffff:ffff:ffff:ffff
 exit
int eth1
 bridge-gr 1
 exit
int eth2
 bridge-gr 1
 exit
int bvi1
 vrf for v1
 ipv4 addr 1.1.1.1 255.255.255.0
 ipv6 addr 1234:1::1 ffff:ffff::
 exit
router bgp4 1
 vrf v1
 address uni
 local-as 1
 router-id 4.4.4.1
 neigh 1.1.1.2 remote-as 1
 neigh 1.1.1.2 soft-reconfig
 neigh 1.1.1.2 route-reflect
 neigh 1.1.1.2 send-comm both
 neigh 1.1.1.3 remote-as 1
 neigh 1.1.1.3 soft-reconfig
 neigh 1.1.1.3 route-reflect
 neigh 1.1.1.3 send-comm both
 red conn
 exit
router bgp6 1
 vrf v1
 address uni
 local-as 1
 router-id 6.6.6.1
 neigh 1234:1::2 remote-as 1
 neigh 1234:1::2 soft-reconfig
 neigh 1234:1::2 route-reflect
 neigh 1234:1::2 send-comm both
 neigh 1234:1::3 remote-as 1
 neigh 1234:1::3 soft-reconfig
 neigh 1234:1::3 route-reflect
 neigh 1234:1::3 send-comm both
 red conn
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
 ipv4 addr 1.1.1.2 255.255.255.0
 ipv6 addr 1234:1::2 ffff:ffff::
 exit
route-map rm1
 set validaspa 1
 exit
router rpki4 1 vrf v1
 exit
router bgp4 1
 vrf v1
 rpki rpki4 1
 address uni
 local-as 1
 router-id 4.4.4.2
 neigh 1.1.1.1 remote-as 1
 neigh 1.1.1.1 soft-reconfig
 neigh 1.1.1.1 send-comm both
 neigh 1.1.1.1 rpki-out rew
 neigh 1.1.1.1 route-map-out rm1
 red conn
 exit
router bgp6 1
 vrf v1
 rpki rpki4 1
 address uni
 local-as 1
 router-id 6.6.6.2
 neigh 1234:1::1 remote-as 1
 neigh 1234:1::1 soft-reconfig
 neigh 1234:1::1 send-comm both
 neigh 1234:1::1 rpki-out rew
 neigh 1234:1::1 route-map-out rm1
 red conn
 exit
!

addrouter r3
int eth1 eth 0000.0000.3333 $2b$ $2a$
!
vrf def v1
 rd 1:1
 exit
int lo0
 vrf for v1
 ipv4 addr 2.2.2.3 255.255.255.255
 ipv6 addr 4321::3 ffff:ffff:ffff:ffff:ffff:ffff:ffff:ffff
 exit
int eth1
 vrf for v1
 ipv4 addr 1.1.1.3 255.255.255.0
 ipv6 addr 1234:1::3 ffff:ffff::
 exit
route-map rm1
 sequence 10 act deny
  match validaspa 1
 sequence 20 act permit
 exit
router rpki4 1 vrf v1
 exit
router bgp4 1
 vrf v1
 rpki rpki4 1
 address uni
 local-as 1
 router-id 4.4.4.3
 neigh 1.1.1.1 remote-as 1
 neigh 1.1.1.1 soft-reconfig
 neigh 1.1.1.1 rpki-in acc
 neigh 1.1.1.1 route-map-in rm1
 neigh 1.1.1.1 send-comm both
 red conn
 exit
router bgp6 1
 vrf v1
 rpki rpki4 1
 address uni
 local-as 1
 router-id 6.6.6.3
 neigh 1234:1::1 remote-as 1
 neigh 1234:1::1 soft-reconfig
 neigh 1234:1::1 rpki-in acc
 neigh 1234:1::1 route-map-in rm1
 neigh 1234:1::1 send-comm both
 red conn
 exit
!


r1 tping 100 60 2.2.2.2 vrf v1
r1 tping 100 60 4321::2 vrf v1
r1 tping 100 60 2.2.2.3 vrf v1
r1 tping 100 60 4321::3 vrf v1

r2 tping 100 60 2.2.2.1 vrf v1
r2 tping 100 60 4321::1 vrf v1
r2 tping 100 60 2.2.2.3 vrf v1
r2 tping 100 60 4321::3 vrf v1

r3 tping 100 60 2.2.2.1 vrf v1
r3 tping 100 60 4321::1 vrf v1
r3 tping 0 60 2.2.2.2 vrf v1
r3 tping 0 60 4321::2 vrf v1
