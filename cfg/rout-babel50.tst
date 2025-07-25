description integrated babel auto summarization

addrouter r1
int eth1 eth 0000.0000.1111 $1a$ $1b$
!
vrf def v1
 rd 1:1
 exit
router babel4 1
 vrf v1
 router 1111-2222-3333-0001
 afi-other enable
 afi-other red conn
 red conn
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
 vrf for v1
 ipv4 addr 2.2.2.21 255.255.255.255
 ipv6 addr 4321::21 ffff:ffff:ffff:ffff:ffff:ffff:ffff:ffff
 exit
route-map p4
 sequence 10 act deny
  match network 2.2.2.12/32
 sequence 20 act perm
  match network 0.0.0.0/0 le 32
 exit
route-map p6
 sequence 10 act deny
  match network 4321::12/128
 sequence 20 act perm
  match network ::/0 le 128
 exit
int eth1
 vrf for v1
 ipv4 addr 1.1.1.1 255.255.255.252
 ipv6 addr 1234:1::1 ffff:ffff::
 router babel4 1 ena
 router babel4 1 other-ena
 router babel4 1 route-map-in p4
 router babel4 1 other-route-map-in p6
 exit
!

addrouter r2
int eth1 eth 0000.0000.2222 $1b$ $1a$
int eth2 eth 0000.0000.2222 $2a$ $2b$
!
vrf def v1
 rd 1:1
 exit
router babel4 1
 vrf v1
 router 1111-2222-3333-0002
 afi-other enable
 afi-other red conn
 afi-other autosumm
 autosumm
 red conn
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
 router babel4 1 ena
 router babel4 1 other-ena
 exit
int eth2
 vrf for v1
 ipv4 addr 1.1.1.5 255.255.255.252
 ipv6 addr 1234:2::1 ffff:ffff::
 router babel4 1 ena
 router babel4 1 other-ena
 exit
!

addrouter r3
int eth1 eth 0000.0000.3333 $2b$ $2a$
!
vrf def v1
 rd 1:1
 exit
router babel4 1
 vrf v1
 router 1111-2222-3333-0002
 afi-other enable
 afi-other red conn
 red conn
 exit
int lo0
 vrf for v1
 ipv4 addr 2.2.2.3 255.255.255.255
 ipv6 addr 4321::3 ffff:ffff:ffff:ffff:ffff:ffff:ffff:ffff
 exit
int lo1
 vrf for v1
 ipv4 addr 2.2.2.12 255.255.255.255
 ipv6 addr 4321::12 ffff:ffff:ffff:ffff:ffff:ffff:ffff:ffff
 exit
int lo2
 vrf for v1
 ipv4 addr 2.2.2.13 255.255.255.255
 ipv6 addr 4321::13 ffff:ffff:ffff:ffff:ffff:ffff:ffff:ffff
 exit
int eth1
 vrf for v1
 ipv4 addr 1.1.1.6 255.255.255.252
 ipv6 addr 1234:2::2 ffff:ffff::
 router babel4 1 ena
 router babel4 1 other-ena
 exit
!

r1 tping 100 130 2.2.2.2 vrf v1
r1 tping 100 130 4321::2 vrf v1
r1 tping 100 130 2.2.2.3 vrf v1
r1 tping 100 130 4321::3 vrf v1
r1 tping 100 130 2.2.2.12 vrf v1
r1 tping 100 130 4321::12 vrf v1

r2 tping 100 130 2.2.2.1 vrf v1
r2 tping 100 130 4321::1 vrf v1
r2 tping 100 130 2.2.2.11 vrf v1
r2 tping 100 130 4321::11 vrf v1
r2 tping 100 130 2.2.2.21 vrf v1
r2 tping 100 130 4321::21 vrf v1

r2 output show ipv4 babel 1 sum
r2 output show ipv6 babel 1 sum
r2 output show ipv4 babel 1 dat
r2 output show ipv6 babel 1 dat
r2 output show ipv4 route v1
r2 output show ipv6 route v1
