#ifdef PROFILE_P
#define HAVE_ALPM
#define HAVE_MPLS
##define HAVE_DUPLAB
##define HAVE_LOCONN
##HAVE_COPP
##HAVE_INACL
##HAVE_INQOS
##HAVE_OUTACL
##HAVE_OUTQOS
##define HAVE_PBR

#define PORT_TABLE_SIZE                        512

#define BUNDLE_TABLE_SIZE                      128

#define VLAN_TABLE_SIZE                        512

#define IPV4_LPM_TABLE_SIZE            114688

#define IPV6_LPM_TABLE_SIZE            28672

#define IPV4_HOST_TABLE_SIZE                   256
#define IPV6_HOST_TABLE_SIZE                   256

#define NEXTHOP_TABLE_SIZE                     512


#define MPLS_TABLE_SIZE                106496
#define _TABLE_SIZE_P4_
#endif
