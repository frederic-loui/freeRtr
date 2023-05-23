{$heap 63k}
{$stack 3k}
{$sysinc system.inc}
{$sysinc alap.inc}
{$sysinc filesys.inc}
{$sysinc param.inc}
{$sysinc pipeline.inc}
{$sysinc bugos.inc}
{$sysinc bignum.inc}
{$sysinc crypto.inc}
{$sysinc random.inc}
{$include \sources\internet\kernel\utils\keys.inc}

{$include random.inc}
{$include \sources\internet\kernel\tls\asn1hdr.inc}
{$include \sources\internet\kernel\tls\asn1num.inc}
{$include \sources\internet\kernel\tls\digsig.inc}
{$include asn1obj.inc}
{$include certify.inc}


Procedure immErr(a:String);
Begin;
WriteLn(a);
halt(1);
End;

Label err;
Var
  buf1,buf2:OnePacketRecord;
  key:OneRSAkeyRecord;
  i,o:LongInt;
  a,b:String;
  f:xFile;
BEGIN;
WriteLn('rsa file signer v1.0, done by Mc at '#%date' '#%time'.');
Randomize;
if CryptoStartActions then immErr('failed to find crypto process!');
if BigNumStartActions then immErr('failed to find bignum process!');

CryptoGetHasherList(key,i);
sha1algoNum:=CryptoFindOneAlgo(key,i,'sha1');
md5algoNum:=CryptoFindOneAlgo(key,i,'md5');
fillchar(key,sizeof(key),0);

if (paramStr(3)='') then immErr('using: signRSAfile.exe <key> <file> <signature>');

WriteLn('going to sign file with RSA key...');


WriteLn('reading key...');
if (xOpen(f,ParamStr(1),xGenFilMod_r)<>0) then immErr('error opening file!');
xBlockRead(f,key,sizeof(key));
xClose(f);

WriteLn('reading file...');
if (xOpen(f,ParamStr(2),xGenFilMod_r)<>0) then immErr('error opening file!');
buf1.s:=xFileSize(f);
if (buf1.s>sizeof(buf1.d)) then immErr('file too large!');
xBlockRead(f,buf1.d,buf1.s);
xClose(f);

WriteLn('coding tags...');
signDataWithRSA(key,buf1,buf2);

WriteLn('writing signature file...');
a:=ParamStr(3);
xErase(a);
xCreate(a);
if (xOpen(f,a,xGenFilMod_rw)<>0) then immErr('error opening target file!');
xSeek(f,0);
xBlockWrite(f,buf2.d,buf2.s);
xTruncate(f);
xClose(f);

WriteLn('successfully finished!');
END.