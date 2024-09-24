description macsec over atmsar

addrouter r1
int atm1 atm - $1a$ $1b$
!
vrf def v1
 rd 1:1
 exit
crypto ipsec ips
 role init
 group 02
 cipher aes256cbc
 hash sha1
 key tester
 exit
int atm1
 atmsar vpi 1
 atmsar vci 2
 vrf for v1
 macsec ips
 ipv4 addr 1.1.1.1 255.255.255.0
 ipv6 addr 1234::1 ffff::
 exit
!

addrouter r2
int atm1 atm - $1b$ $1a$
!
vrf def v1
 rd 1:1
 exit
crypto ipsec ips
 role init
 group 02
 cipher aes256cbc
 hash sha1
 key tester
 exit
int atm1
 atmsar vpi 1
 atmsar vci 2
 vrf for v1
 macsec ips
 ipv4 addr 1.1.1.2 255.255.255.0
 ipv6 addr 1234::2 ffff::
 exit
!


r1 tping 100 30 1.1.1.2 vrf v1
r2 tping 100 30 1.1.1.1 vrf v1
r1 tping 100 30 1234::2 vrf v1
r2 tping 100 30 1234::1 vrf v1
