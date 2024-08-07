from ..bf_gbl_env.var_env import *


def writeVlanRules(self, op_type, port, main, vlan):
    # for any reason, control plane is sending a msg
    # with port=-1
    if port < 0:
        return

    tbl_global_path_1 = "ig_ctl.ig_ctl_vlan_in"
    tbl_name_1 = "%s.tbl_vlan_in" % (tbl_global_path_1)
    tbl_action_name_1 = "%s.act_set_vlan_iface" % (tbl_global_path_1)
    key_field_list_1 = [
        gc.KeyTuple("ig_md.ingress_id", main),
        gc.KeyTuple("hdr.vlan.vid", vlan),
        gc.KeyTuple("hdr.vlanq.vid", 0),
    ]
    data_field_list_1 = [gc.DataTuple("src", port)]

    key_annotation_fields_1 = {}
    data_annotation_fields_1 = {}

    self._processEntryFromControlPlane(
        op_type,
        tbl_name_1,
        key_field_list_1,
        data_field_list_1,
        tbl_action_name_1,
        key_annotation_fields_1,
        data_annotation_fields_1,
    )

    tbl_global_path_2 = "eg_ctl.eg_ctl_vlan_out"
    tbl_name_2 = "%s.tbl_vlan_out" % (tbl_global_path_2)
    tbl_action_name_2 = "%s.act_set_vlan_port" % (tbl_global_path_2)
    key_field_list_2 = [gc.KeyTuple("eg_md.target_id", port)]
    data_field_list_2 = [gc.DataTuple("port", main), gc.DataTuple("vlan", vlan)]
    key_annotation_fields_2 = {}
    data_annotation_fields_2 = {}

    self._processEntryFromControlPlane(
        op_type,
        tbl_name_2,
        key_field_list_2,
        data_field_list_2,
        tbl_action_name_2,
        key_annotation_fields_2,
        data_annotation_fields_2,
    )

    tbl_global_path_3 = "ig_ctl.ig_ctl_outport"
    tbl_name_3 = "%s.tbl_vlan_out" % (tbl_global_path_3)
    tbl_action_name_3 = "%s.act_set_port_vlan" % (tbl_global_path_3)
    key_field_list_3 = [gc.KeyTuple("ig_md.target_id", port)]
    data_field_list_3 = [gc.DataTuple("port", main)]
    key_annotation_fields_3 = {}
    data_annotation_fields_3 = {}

    self._processEntryFromControlPlane(
        op_type,
        tbl_name_3,
        key_field_list_3,
        data_field_list_3,
        tbl_action_name_3,
        key_annotation_fields_3,
        data_annotation_fields_3,
    )
