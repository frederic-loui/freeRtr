description interop1: bgp vpnv6 over srv6

addrouter r1
int eth1 eth 0000.0000.1111 $per1$
!
vrf def v1
 rd 1:1
 exit
vrf def v2
 rd 1:2
 rt-both 1:2
 exit
vrf def v3
 rd 1:3
 rt-both 1:3
 exit
int eth1
 vrf for v1
 ipv4 addr 1.1.1.1 255.255.255.0
 ipv6 addr 1234::1 ffff::
 exit
int tun1
 vrf for v1
 ipv6 addr 2222:: ffff:ffff::
 tun sour eth1
 tun dest 2222::
 tun vrf v1
 tun mod srv6
 exit
ipv4 route v1 2.2.2.2 255.255.255.255 1.1.1.2
ipv6 route v1 1111:: ffff:: 1234::2
ipv6 route v1 4321::2 ffff:ffff:ffff:ffff:ffff:ffff:ffff:ffff 1234::2
int lo0
 vrf for v1
 ipv4 addr 2.2.2.1 255.255.255.255
 ipv6 addr 4321::1 ffff:ffff:ffff:ffff:ffff:ffff:ffff:ffff
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
router bgp4 1
 vrf v1
 address vpnuni
 local-as 1
 router-id 4.4.4.1
 neigh 1.1.1.2 remote-as 2
 neigh 1.1.1.2 send-comm both
 neigh 1.1.1.2 segrou
 afi-vrf v2 ena
 afi-vrf v2 srv6 tun1
 afi-vrf v2 red conn
 afi-vrf v3 ena
 afi-vrf v3 srv6 tun1
 afi-vrf v3 red conn
 exit
router bgp6 1
 vrf v1
 address vpnuni
 local-as 1
 router-id 6.6.6.1
 neigh 1234::2 remote-as 2
 neigh 1234::2 send-comm both
 neigh 1234::2 segrou
 afi-vrf v2 ena
 afi-vrf v2 srv6 tun1
 afi-vrf v2 red conn
 afi-vrf v3 ena
 afi-vrf v3 srv6 tun1
 afi-vrf v3 red conn
 exit
!

addpersist r2
int eth1 eth 0000.0000.2222 $per1$
!
ip routing
ipv6 unicast-routing
vrf definition v2
 rd 1:2
 route-target export 1:2
 route-target import 1:2
 address-family ipv4
 address-family ipv6
 exit
vrf definition v3
 rd 1:3
 route-target export 1:3
 route-target import 1:3
 address-family ipv4
 address-family ipv6
 exit
interface loopback0
 ip addr 2.2.2.2 255.255.255.255
 ipv6 addr 4321::2/128
 exit
interface loopback2
 vrf forwarding v2
 ip address 9.9.2.2 255.255.255.255
 ipv6 address 9992::2/128
 exit
interface loopback3
 vrf forwarding v3
 ip address 9.9.3.2 255.255.255.255
 ipv6 address 9993::2/128
 exit
interface gigabit1
 ip address 1.1.1.2 255.255.255.0
 ipv6 address 1234::2/64
 no shutdown
 exit
segment-routing srv6
 encapsulation
  source-address 4321::2
 locators
  locator a
   prefix 1111:1111:1111::/48
   format usid-f3216
ip route 2.2.2.1 255.255.255.255 1.1.1.1
ipv6 route 2222::/48 1234::1
ipv6 route 4321::1/128 1234::1
router bgp 2
 neighbor 1234::1 remote-as 1
 neighbor 1234::1 disable-connected-check
 segment-routing srv6
  locator a
 address-family vpnv4 unicast
  neighbor 1234::1 activate
  neighbor 1234::1 send-community both
  neighbor 1234::1 encap srv6
  segment-routing srv6
   locator a
 address-family vpnv6 unicast
  neighbor 1234::1 activate
  neighbor 1234::1 send-community both
  neighbor 1234::1 encap srv6
  segment-routing srv6
   locator a
 address-family ipv4 vrf v2
  redistribute connected
  segment-routing srv6
   locator a
   alloc-mode per-vrf
 address-family ipv6 vrf v2
  redistribute connected
  segment-routing srv6
   locator a
   alloc-mode per-vrf
 address-family ipv4 vrf v3
  redistribute connected
  segment-routing srv6
   locator a
   alloc-mode per-vrf
 address-family ipv6 vrf v3
  redistribute connected
  segment-routing srv6
   locator a
   alloc-mode per-vrf
 exit
!


r1 tping 100 10 1.1.1.2 vrf v1
r1 tping 100 10 1234::2 vrf v1
r1 tping 100 120 2.2.2.2 vrf v1 sou lo0
r1 tping 100 120 4321::2 vrf v1 sou lo0
!r1 tping 100 120 9.9.2.2 vrf v2
r1 tping 100 120 9992::2 vrf v2
!r1 tping 100 120 9.9.3.2 vrf v3
r1 tping 100 120 9993::2 vrf v3
