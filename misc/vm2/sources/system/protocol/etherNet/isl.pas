{$heap 31k}
{$stack 3k}
{$sysinc system.inc}
{$sysinc param.inc}
{$sysinc pipeline.inc}
{$sysinc bugos.inc}
{$sysinc memory.inc}
{$sysinc hex.inc}
{$include \sources\internet\kernel\utils\timer2.inc}
{$include \sources\utils\protocol\crc32.inc}

Type
  oneDescriptorRecord=record
    num:LongInt;
    pipe:LongInt;
    proc:LongInt;
    end;
Var
  vlanDat:array[1..128] of oneDescriptorRecord;
  vlanNum:LongInt;
  addrSiz:LongInt;
  packSiz:LongInt;
  localAddr:array[1..32] of byte;
  broadAddr:array[1..32] of byte;
  deviceName:String;
  ioBase:LongInt;
  memBase:LongInt;




Procedure ImmErr(a:String);
Begin;
WriteLn(a);
Halt(2);
End;

Function conv2hex(i:LongInt):String;
Begin;
conv2hex:=byte2hextype(i shr 24)+byte2hextype(i shr 16)+byte2hextype(i shr 8)+byte2hextype(i);
End;

Function convAddr(var data):String;
Var
  d:array[1..1] of byte absolute data;
  i:LongInt;
  a:String;
Begin;
a:='';
for i:=1 to addrSiz do a:=a+'-'+byte2hextype(d[i]);
convAddr:=copy(a,2,666);
End;

Function findOneVLanID(d:LongInt):LongInt;
Label f1;
Var i:LongInt;
Begin;
for i:=1 to vlanNum do if (vlanDat[i].num=d) then goto f1;
i:=0;
f1:
findOneVLanID:=i;
End;



Label f1;
Var
  a:String;
  pipe:LongInt;
  i,o,p,q:LongInt;
  buf:array[1..1024*8] of byte;
BEGIN;
WriteLn('isl v1.0, done by Mc at '#%date' '#%time'.');
crc32build;

vlanNum:=0;
for i:=2 to paramCount do begin;
  o:=BVal(paramStr(i));
  if (o=0) then continue;
  inc(vlanNum);
  vlanDat[vlanNum].num:=o and $fff;
  vlanDat[vlanNum].pipe:=0;
  end;
if (vlanNum<1) then immErr('using: isl.code <process> <vlanid> [vlanid]...');

a:=ParamStr(1);
WriteLn('process: '+a);
o:=BugOS_findProcNam(a);
if (o=0) then immErr('process not found!');
WriteLn('process#: '+BStr(o));
i:=pipeLineCreate(pipe,o,65536,true);
if (i<>0) then immErr('unabled to create pipeline!');
WriteLn('pipeline#: '+BStr(pipe));
for i:=1 to 16 do relequish;
i:=sizeof(buf);
if (pipeLineRecv(pipe,buf,i)<>0) then i:=0;
if (i<1) then immErr('initial packet not received!');
move(buf[1],addrSiz,sizeof(addrSiz));
move(buf[5],packSiz,sizeof(packSiz));
move(buf[9],ioBase,sizeof(ioBase));
move(buf[13],memBase,sizeof(memBase));
o:=17;
move(buf[o],localAddr,sizeof(localAddr));inc(o,addrSiz);
move(buf[o],broadAddr,sizeof(broadAddr));inc(o,addrSiz);
deviceName:='';
while (buf[o]<>0) do begin;
  deviceName:=deviceName+chr(buf[o]);
  inc(o);
  end;
WriteLn('address size: '+BStr(addrSiz));
WriteLn('packet size: '+BStr(packSiz));
WriteLn('io base: '+conv2hex(ioBase));
WriteLn('mem base: '+conv2hex(memBase));
WriteLn('station address: '+convAddr(localAddr));
WriteLn('broadcast address: '+convAddr(broadAddr));
WriteLn('device name: "'+deviceName+'"');
Write('vlans:');
for i:=1 to vlanNum do Write(' '+BStr(vlanDat[i].num));
WriteLn('');

pipeLineBegListen;
BugOS_SignDaemoning;

f1:
o:=sizeof(buf);
if (pipeLineRecv(pipe,buf,o)=0) then begin;
  i:=readWordMSB(buf[addrSiz+1]);
  if (i+addrSiz+2>o) then begin;
    WriteLn('got truncated packet from '+convAddr(buf));
    goto f1;
    end;
  o:=i-addrSiz-16;
  if (o<0) then begin;
    WriteLn('got wrong size from '+convAddr(buf));
    goto f1;
    end;
  if (readWordMSB(buf[addrSiz+3])<>$aaaa) then begin;
    WriteLn('got wrong sap from '+convAddr(buf));
    goto f1;
    end;
  if (readLongMSB(buf[addrSiz+5])<>$0300000c) then begin;
    WriteLn('got wrong ctl/hsa from '+convAddr(buf));
    goto f1;
    end;
  i:=(readWordMSB(buf[addrSiz+9]) shr 1) and $fff;
  p:=findOneVLanID(i);
  if (p<1) then begin;
    WriteLn('got invalid vlan id ('+BStr(i)+') packet from '+convAddr(buf));
    goto f1;
    end;
  if (vlanDat[p].pipe=0) then goto f1;
  pipeLineSend(vlanDat[p].pipe,buf[addrSiz+addrSiz+15],o);
  goto f1;
  end;
while (pipeLineGetIncoming(p)=0) do begin;
  pipeLineStats(p,q,i,i);
  BugOS_ProcessName(q,buf,i,i,o);
  if (o and $40=0) then begin; pipeLineClose(p);break; end;
  o:=0;
  for i:=1 to vlanNum do if (vlanDat[i].pipe=0) then begin; o:=i;break; end;
  if (o=0) then begin; pipeLineClose(p);break; end;
  vlanDat[o].pipe:=p;
  vlanDat[o].proc:=q;
  move(addrSiz,buf[1],sizeof(addrSiz));
  i:=packSiz-4;
  move(i,buf[5],sizeof(i));
  move(ioBase,buf[9],sizeof(ioBase));
  move(memBase,buf[13],sizeof(memBase));
  i:=17;
  move(localAddr,buf[i],addrSiz);inc(i,addrSiz);
  move(broadAddr,buf[i],addrSiz);inc(i,addrSiz);
  a:='isl'+BStr(vlanDat[o].num)+' on '+deviceName;
  move(a[1],buf[i],sizeof(a));
  inc(i,length(a));
  buf[i]:=0;
  pipeLineSend(vlanDat[o].pipe,buf,i);
  WriteLn('vlan '+BStr(vlanDat[o].num)+' logged in!');
  end;
for q:=1 to vlanNum do begin;
  if (vlanDat[q].pipe=0) then continue;
  o:=sizeof(buf);
  if (pipeLineRecv(vlanDat[q].pipe,buf,o)<>0) then o:=0;
  if (o<1) then begin;
    pipeLineStats(vlanDat[q].pipe,o,i,i);
    if (o<>0) then continue;
    pipeLineClose(vlanDat[q].pipe);
    vlanDat[q].pipe:=0;
    WriteLn('vlan '+BStr(vlanDat[q].num)+' logged out!');
    continue;
    end;
  move(buf[addrSiz+1],buf[addrSiz+addrSiz+1],o);
  move(localAddr,buf[addrSiz+1],addrSiz);
  inc(o,addrSiz);
  p:=$ffffffff;
  crc32update(p,buf,o);
  WriteLongLSB(buf[o+1],not p);
  inc(o,sizeof(p));
  move(buf,buf[21],o);
  WriteLongMSB(buf[1],$01000c00);
  WriteLongMSB(buf[5],o+12);
  WriteLongMSB(buf[9],$aaaa0300);
  WriteLongMSB(buf[13],(vlanDat[q].num shl 1) or $000c0000);
  WriteLongMSB(buf[17],0);
  pipeLineSend(pipe,buf,o+20);
  end;
relequish;
goto f1;
END.