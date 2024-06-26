description cross connect ethernet interfaces

addrouter r1
int eth1 eth 0000.0000.1111 $1a$ $1b$
!
vrf def v1
 rd 1:1
 exit
int eth1
 vrf for v1
 ipv4 addr 2.2.2.1 255.255.255.0
 ipv6 addr 4321::1 ffff::
 exit
!

addrouter r2
int eth1 eth 0000.0000.2222 $1b$ $1a$
int eth2 eth 0000.0000.2222 $2a$ $2b$
!
int eth1
 connect eth2
 exit
int eth2
 exit
!

addrouter r3
int eth1 eth - $2b$ $2a$
!
vrf def v1
 rd 1:1
 exit
int eth1
 vrf for v1
 ipv4 addr 2.2.2.2 255.255.255.0
 ipv6 addr 4321::2 ffff::
 exit
!



r1 tping 100 30 2.2.2.2 vrf v1
r1 tping 100 30 4321::2 vrf v1
r3 tping 100 30 2.2.2.1 vrf v1
r3 tping 100 30 4321::1 vrf v1
