description integrated isis change in tag

addrouter r1
int eth1 eth 0000.0000.1111 $1a$ $1b$
!
vrf def v1
 rd 1:1
 exit
route-map rm1
 set tag set 1000
 exit
router isis4 1
 vrf v1
 net 11.4444.0000.1111.00
 red conn route-map rm1
 afi-other enable
 afi-other red conn route-map rm1
 exit
int lo1
 vrf for v1
 ipv4 addr 2.2.2.1 255.255.255.255
 ipv6 addr 4321::1 ffff:ffff:ffff:ffff:ffff:ffff:ffff:ffff
 exit
int eth1
 vrf for v1
 ipv4 addr 1.1.1.1 255.255.255.252
 ipv6 addr 1234:1::1 ffff:ffff::
 router isis4 1 ena
 router isis4 1 other-ena
 exit
!

addrouter r2
int eth1 eth 0000.0000.2222 $1b$ $1a$
int eth2 eth 0000.0000.2222 $2a$ $2b$
!
vrf def v1
 rd 1:1
 exit
route-map rm1
 sequence 10 act deny
 sequence 10 match tag 2000-4000
 sequence 20 act perm
 exit
router isis6 1
 vrf v1
 net 22.6666.0000.2222.00
 red conn
 afi-other enable
 afi-other red conn
 both route-map-from rm1
 both other-route-map-from rm1
 exit
int lo1
 vrf for v1
 ipv4 addr 2.2.2.2 255.255.255.255
 ipv6 addr 4321::2 ffff:ffff:ffff:ffff:ffff:ffff:ffff:ffff
 exit
int eth1
 vrf for v1
 ipv4 addr 1.1.1.2 255.255.255.252
 ipv6 addr 1234:1::2 ffff:ffff::
 router isis6 1 ena
 router isis6 1 other-ena
 exit
int eth2
 vrf for v1
 ipv4 addr 1.1.1.5 255.255.255.252
 ipv6 addr 1234:2::1 ffff:ffff::
 router isis6 1 ena
 router isis6 1 other-ena
 exit
!

addrouter r3
int eth1 eth 0000.0000.3333 $2b$ $2a$
!
vrf def v1
 rd 1:1
 exit
router isis4 1
 vrf v1
 net 22.4444.0000.3333.00
 red conn
 afi-other enable
 afi-other red conn
 exit
int lo1
 vrf for v1
 ipv4 addr 2.2.2.3 255.255.255.255
 ipv6 addr 4321::3 ffff:ffff:ffff:ffff:ffff:ffff:ffff:ffff
 exit
int eth1
 vrf for v1
 ipv4 addr 1.1.1.6 255.255.255.252
 ipv6 addr 1234:2::2 ffff:ffff::
 router isis4 1 ena
 router isis4 1 other-ena
 exit
!


r1 tping 100 20 2.2.2.2 /vrf v1
r1 tping 100 20 4321::2 /vrf v1
r1 tping 100 20 2.2.2.3 /vrf v1
r1 tping 100 20 4321::3 /vrf v1
r2 tping 100 20 2.2.2.1 /vrf v1
r2 tping 100 20 4321::1 /vrf v1
r2 tping 100 20 2.2.2.3 /vrf v1
r2 tping 100 20 4321::3 /vrf v1
r3 tping 100 20 2.2.2.1 /vrf v1
r3 tping 100 20 4321::1 /vrf v1
r3 tping 100 20 2.2.2.2 /vrf v1
r3 tping 100 20 4321::2 /vrf v1

r1 send conf t
r1 send route-map rm1
r1 send set tag set 3000
r1 send end
r1 send clear ipv4 route v1
r1 send clear ipv6 route v1

r1 tping 100 20 2.2.2.2 /vrf v1
r1 tping 100 20 4321::2 /vrf v1
r1 tping 100 20 2.2.2.3 /vrf v1
r1 tping 100 20 4321::3 /vrf v1
r2 tping 0 20 2.2.2.1 /vrf v1
r2 tping 0 20 4321::1 /vrf v1
r2 tping 100 20 2.2.2.3 /vrf v1
r2 tping 100 20 4321::3 /vrf v1
r3 tping 0 20 2.2.2.1 /vrf v1
r3 tping 0 20 4321::1 /vrf v1
r3 tping 100 20 2.2.2.2 /vrf v1
r3 tping 100 20 4321::2 /vrf v1

r1 send conf t
r1 send route-map rm1
r1 send set tag set 5000
r1 send end
r1 send clear ipv4 route v1
r1 send clear ipv6 route v1

r1 tping 100 20 2.2.2.2 /vrf v1
r1 tping 100 20 4321::2 /vrf v1
r1 tping 100 20 2.2.2.3 /vrf v1
r1 tping 100 20 4321::3 /vrf v1
r2 tping 100 20 2.2.2.1 /vrf v1
r2 tping 100 20 4321::1 /vrf v1
r2 tping 100 20 2.2.2.3 /vrf v1
r2 tping 100 20 4321::3 /vrf v1
r3 tping 100 20 2.2.2.1 /vrf v1
r3 tping 100 20 4321::1 /vrf v1
r3 tping 100 20 2.2.2.2 /vrf v1
r3 tping 100 20 4321::2 /vrf v1

r2 output show ipv4 isis 1 nei
r2 output show ipv6 isis 1 nei
r2 output show ipv4 isis 1 dat 2
r2 output show ipv6 isis 1 dat 2
r2 output show ipv4 isis 1 tre 2
r2 output show ipv6 isis 1 tre 2
r2 output show ipv4 route v1
r2 output show ipv6 route v1
