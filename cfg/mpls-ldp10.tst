description ldp over ipsec

addrouter r1
int eth1 eth 0000.0000.1111 $1a$ $1b$
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
 deny all 4321:: ffff:: all 4321:: ffff:: all
 permit all any all any all
 exit
int lo0
 vrf for v1
 ipv4 addr 2.2.2.1 255.255.255.255
 ipv6 addr 4321::1 ffff:ffff:ffff:ffff:ffff:ffff:ffff:ffff
 exit
int eth1
 vrf for v1
 ipv6 addr 1111::1 ffff::
 exit
crypto ipsec ips
 group 02
 cipher des
 hash md5
 seconds 3600
 bytes 1024000
 key tester
 role static
 isakmp 1
 protected ipv4
 exit
int tun1
 tunnel vrf v1
 tunnel prot ips
 tunnel mode ipsec
 tunnel source ethernet1
 tunnel destination 1111::2
 vrf for v1
 ipv4 addr 1.1.1.1 255.255.255.0
 ipv6 addr 1234::1 ffff::
 ipv4 access-group-in test4
 ipv6 access-group-in test6
 no ipv4 unreachables
 no ipv6 unreachables
 mpls enable
 mpls ldp4
 mpls ldp6
 exit
ipv4 route v1 2.2.2.2 255.255.255.255 1.1.1.2
ipv6 route v1 4321::2 ffff:ffff:ffff:ffff:ffff:ffff:ffff:ffff 1234::2
!

addrouter r2
int eth1 eth 0000.0000.2222 $1b$ $1a$
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
 deny all 4321:: ffff:: all 4321:: ffff:: all
 permit all any all any all
 exit
int lo0
 vrf for v1
 ipv4 addr 2.2.2.2 255.255.255.255
 ipv6 addr 4321::2 ffff:ffff:ffff:ffff:ffff:ffff:ffff:ffff
 exit
int eth1
 vrf for v1
 ipv6 addr 1111::2 ffff::
 exit
crypto ipsec ips
 group 02
 cipher des
 hash md5
 seconds 3600
 bytes 1024000
 key tester
 role static
 isakmp 1
 protected ipv4
 exit
int tun1
 tunnel vrf v1
 tunnel prot ips
 tunnel mode ipsec
 tunnel source ethernet1
 tunnel destination 1111::1
 vrf for v1
 ipv4 addr 1.1.1.2 255.255.255.0
 ipv6 addr 1234::2 ffff::
 ipv4 access-group-in test4
 ipv6 access-group-in test6
 no ipv4 unreachables
 no ipv6 unreachables
 mpls enable
 mpls ldp4
 mpls ldp6
 exit
ipv4 route v1 2.2.2.1 255.255.255.255 1.1.1.1
ipv6 route v1 4321::1 ffff:ffff:ffff:ffff:ffff:ffff:ffff:ffff 1234::1
!


r1 tping 100 10 2.2.2.2 vrf v1 sou lo0
r1 tping 100 10 4321::2 vrf v1 sou lo0
r2 tping 100 10 2.2.2.1 vrf v1 sou lo0
r2 tping 100 10 4321::1 vrf v1 sou lo0
r1 tping 0 10 1.1.1.2 vrf v1
r2 tping 0 10 1.1.1.1 vrf v1
