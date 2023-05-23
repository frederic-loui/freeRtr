{$stack 1k}
{$heap 16k}
{$sysinc system.inc}
{$sysinc param.inc}
{$sysinc alap.inc}
{$sysinc filesys.inc}
{$sysinc filesys2.inc}
{$sysinc textfile.inc}
Const
  RegNames:array[1..3,1..6] of string[3]=(
    ('al','bl','cl','dl','?','?'),
    ('ax','bx','cx','dx','si','di'),
    ('eax','ebx','ecx','edx','esi','edi')  );
  CondCods:array[false..true,1..6] of string[3]=(
    ('a','b','ne','e','ae','be'),
    ('g','l','ne','e','ge','le')  );
  CompareSeenSign:Boolean=true;
  ProcedureSeenName:String='';
Var tout,tinp,ttbl:xtText;


Procedure outputLine(a,b:String);
Begin;
if (b<>'') then begin;
  while (length(a)<30) do a:=a+' ';
  a:=a+' ;'+b;
  end;
xtWriteLn(tout,a);
End;

Function getBaseName:String;
Var
  i,o:Word;
  a:String;
Begin;
a:=ParamStr(3);
if (a<>'') then begin; getBaseName:=a;exit; end;
a:=ParamStr(0);
o:=0;
for i:=1 to length(a) do if (a[i]='\') then o:=i;
a:=copy(a,o+1,666);
o:=666;
for i:=1 to length(a) do if (a[i]='.') then o:=i;
a:='c:\sources\developer\vm-compiler\'+copy(a,1,o-1)+'.table';
getBaseName:=a;
End;

Function getWord(var a:String):String;
Var i:Word;
Begin;
i:=pos(' ',a);
if (i<1) then i:=$666;
getWord:=copy(a,1,i-1);
a:=copy(a,i+1,$666);
End;

function getSizes(var a:string):integer;
var i:integer;
begin;
a[length(a)+1]:=#255;
case a[1] of
  'b':i:=1;
  'w':i:=2;
  'd':i:=3;
  else i:=-1;
  end;
delete(a,1,1);
getSizes:=i;
end;

function getSigns(var a:string):integer;
var i:integer;
begin;
a[length(a)+1]:=#255;
case a[1] of
  's':i:=1;
  'u':i:=2;
  else i:=-1;
  end;
delete(a,1,1);
getSigns:=i;
end;

function getFormats(var a:string):integer;
var i:integer;
begin;
a[length(a)+1]:=#255;
case a[1] of
  'd':i:=1;
  'm':i:=2;
  'l':i:=3;
  else i:=-1;
  end;
delete(a,1,1);
getFormats:=i;
end;

function getRegister(a:string;dat,ptr:boolean):integer;
var i:integer;
begin;
i:=-1;
if dat then begin;
  if (a='a') then i:=1;
  if (a='b') then i:=2;
  if (a='c') then i:=3;
  if (a='d') then i:=4;
  end;
if ptr then begin;
  if (a='src') then i:=5;
  if (a='trg') then i:=6;
  end;
getRegister:=i;
end;

function getCondition(a:string):integer;
var i:integer;
begin;
i:=-1;
if (a='nbe') then a:='a';
if (a='nae') then a:='b';
if (a='nb') then a:='ae';
if (a='na') then a:='be';
if (a='a') then i:=1;
if (a='b') then i:=2;
if (a='ne') then i:=3;
if (a='e') then i:=4;
if (a='ae') then i:=5;
if (a='be') then i:=6;
getCondition:=i;
end;

function getMemory(a:string):string;
var o:longint;
begin;
getMemory:='';
if (copy(a,1,1)<>'[') then exit;
if (copy(a,length(a),1)<>']') then exit;
a:=copy(a,2,length(a)-2);
if (length(a)=3) then a:=a+'+0';
o:=getRegister(copy(a,1,3),false,true);
if (o<1) then exit;
a:=copy(a,4,$666);
getMemory:='['+RegNames[3][o]+a+']';
end;

function getNumReg(a:string;siz:byte):string;
Var i:LongInt;
begin;
siz:=siz and 15;
i:=getRegister(a,true,true);
if (i>0) then a:=RegNames[siz][i];
getNumReg:=a;
end;


Function CompileOneLine(c:String;lineNum:LongInt):Boolean;
Label oke,vege;
Var
  siz1,siz2,siz3:integer;
  sig1,sig2,sig3:integer;
  reg1,reg2,reg3:integer;
  oc,a,d:String;
  i,o,p:LongInt;

procedure CodePushPop(cmd:string;num,siz:byte);
begin;
siz:=siz and 15;
num:=num and 15;
if (siz<2) then siz:=2;
outputLine(cmd+' '+RegNames[siz][num],oc);
oc:='';
end;

function CodeTheDividing(modulus:boolean):boolean;
label f1;

procedure divit;
var a,b:string[7];
begin;
if (siz1>1) and (reg1<>4) then CodePushPop('push',4,siz1);
if (sig1=1) then case siz1 of
  1:outputLine('cbw',oc);
  2:outputLine('cwd',oc);
  3:outputLine('cdq',oc);
  end else case siz1 of
  1:outputLine('mov ah,0',oc);
  2:outputLine('sub dx,dx',oc);
  3:outputLine('sub edx,edx',oc);
  end;
if (sig1=1) then a:='i' else a:='';
outputLine(a+'div '+RegNames[siz1][reg3],'');
if modulus then begin;
  a:=RegNames[siz1][4];
  if (siz1=1) then a:='ah';
  end else a:=RegNames[siz1][1];
b:=RegNames[siz1][reg1];
if (b<>a) then outputLine('mov '+b+','+a,'');
oc:='';
if (siz1>1) and (reg1<>4) then CodePushPop('pop',4,siz1);
end;

begin;
CodeTheDividing:=True;
a:=getWord(c);
sig1:=getSigns(a);
if (sig1<0) then exit;
siz1:=getSizes(a);
if (siz1<0) then exit;
a:=getWord(c);
reg1:=getRegister(a,true,true);
if (reg1<0) then exit;
a:=getWord(c);
reg2:=getRegister(a,true,true);
reg3:=reg2;
if (reg1=reg2) then begin;
  a:=RegNames[siz1][reg1];
  if modulus then outputLine('sub '+a+','+a,oc) else outputLine('mov '+a+',1',oc);
  goto f1;
  end;
if (reg1=1) then begin;
  if (reg2=4) then begin; {a,d}
    if (siz1>1) then begin;
      reg3:=3;
      CodePushPop('push',reg3,siz1);
      outputLine('mov '+RegNames[siz1][reg3]+','+RegNames[siz1][reg2],'');
      end;
    divit;
    if (siz1>1) then CodePushPop('pop',reg3,siz1);
    goto f1;
    end;
  if (reg2<0) then begin; {a,5}
    reg3:=3;
    CodePushPop('push',reg3,siz1);
    outputLine('mov '+RegNames[siz1][reg3]+','+a,'');
    divit;
    CodePushPop('pop',reg3,siz1);
    goto f1;
    end;
  {a,c}
  divit;
  goto f1;
  end;
if (reg1=4) then begin;
  if (reg2=1) then begin; {d,a}
    reg3:=3;
    CodePushPop('push',reg2,siz1);
    CodePushPop('push',reg3,siz1);
    outputLine('mov '+RegNames[siz1][reg3]+','+RegNames[siz1][reg2],'');
    outputLine('mov '+RegNames[siz1][reg2]+','+RegNames[siz1][reg1],'');
    divit;
    CodePushPop('pop',reg3,siz1);
    CodePushPop('pop',reg2,siz1);
    goto f1;
    end;
  if (reg2<0) then begin; {d,5}
    reg3:=3;
    CodePushPop('push',1,siz1);
    CodePushPop('push',reg3,siz1);
    outputLine('mov '+RegNames[siz1][reg3]+','+a,'');
    outputLine('mov '+RegNames[siz1][1]+','+RegNames[siz1][reg1],'');
    divit;
    CodePushPop('pop',reg3,siz1);
    CodePushPop('pop',1,siz1);
    goto f1;
    end;
  {d,c}
  CodePushPop('push',1,siz1);
  outputLine('mov '+RegNames[siz1][1]+','+RegNames[siz1][reg1],'');
  divit;
  CodePushPop('pop',1,siz1);
  goto f1;
  end;
if (reg1 in [2,3]) then begin;
  if (reg2=1) then begin; {c,a}
    reg3:=reg1;
    reg1:=1;
    reg2:=reg3;
    outputLine('xchg '+RegNames[siz1][reg3]+','+RegNames[siz1][1],oc);oc:='';
    divit;
    outputLine('xchg '+RegNames[siz1][reg3]+','+RegNames[siz1][1],'');
    goto f1;
    end;
  if (reg2=4) then begin; {c,d}
    CodePushPop('push',1,siz1);
    if (siz1>1) then begin;
      reg3:=5-reg1;
      CodePushPop('push',reg3,siz1);
      outputLine('mov '+RegNames[siz1][reg3]+','+RegNames[siz1][reg2],'');
      end;
    outputLine('mov '+RegNames[siz1][1]+','+RegNames[siz1][reg1],'');
    divit;
    if (siz1>1) then CodePushPop('pop',reg3,siz1);
    CodePushPop('pop',1,siz1);
    goto f1;
    end;
  if (reg2<0) then begin; {c,5}
    reg3:=5-reg1;
    CodePushPop('push',1,siz1);
    CodePushPop('push',reg3,siz1);
    outputLine('mov '+RegNames[siz1][reg3]+','+a,'');
    outputLine('mov '+RegNames[siz1][1]+','+RegNames[siz1][reg1],'');
    divit;
    CodePushPop('pop',reg3,siz1);
    CodePushPop('pop',1,siz1);
    goto f1;
    end;
  {c,b}
  CodePushPop('push',1,siz1);
  outputLine('mov '+RegNames[siz1][1]+','+RegNames[siz1][reg1],'');
  divit;
  CodePushPop('pop',1,siz1);
  goto f1;
  end;

exit;
f1:
CodeTheDividing:=false;
end;

Function CodeTheMemoryio(read:Boolean):Boolean;
Var par1,par2:String;

procedure msbit;
begin;
a:=RegNames[1][reg2];
a:=a[1]+'l,'+a[1]+'h';
case siz3 of
  2:outputLine('xchg '+a,'');
  3:begin;
    outputLine('xchg '+a,'');
    outputLine('rol '+RegNames[3][reg2]+',16','');
    outputLine('xchg '+a,'');
    end;
  end;
end;

Begin;
CodeTheMemoryio:=True;
a:=getWord(c);
reg3:=getFormats(a);
if (reg3<0) then exit;
sig1:=getSigns(a);
if (sig1<0) then exit;
siz1:=getSizes(a);
if (siz1<0) then exit;
sig2:=getSigns(a);
if (sig2<0) then exit;
siz2:=getSizes(a);
if (siz2<0) then exit;
par1:=getWord(c);
par2:=getWord(c);
if (siz1<=siz2) then siz2:=siz1;
if read then begin;
  a:=par1;par1:=par2;par2:=a;
  i:=siz1;siz1:=siz2;siz2:=i;
  i:=sig1;sig1:=sig2;sig2:=i;
  end;
par1:=getMemory(par1);
if (par1='') then exit;
reg2:=getRegister(par2,true,true);
if (reg2<0) then exit;
par2:=RegNames[siz2][reg2];
siz3:=siz1;if (siz3<siz2) then siz3:=siz2;
if (reg3=2) and (siz3>1) then begin;
  sig3:=siz2;
  if not read then begin;
    CodePushPop('push',reg2,siz2);
    if (sig1=1) and (sig2=1) then a:='movsx' else a:='movzx';
    if (siz2<siz3) then outputLine(a+' '+RegNames[siz3][reg2]+','+RegNames[siz2][reg2],'');
    msbit;
    siz1:=siz3;
    siz2:=siz3;
    par2:=RegNames[siz2][reg2];
    end;
  end;
if (sig1=1) and (sig2=1) then d:='movsx' else d:='movzx';
case siz1 of
  1:a:='byte';
  2:a:='word';
  3:a:='dword';
  else exit;
  end;
par1:=a+' ptr '+par1;
if (siz1=siz2) then d:='mov';
if read then a:=par2+','+par1 else a:=par1+','+par2;
d:=d+' '+a;
outputLine(d,oc);oc:='';
if (reg3=2) and (siz3>1) then begin;
  if read then msbit else CodePushPop('pop',reg2,sig3);
  end;
CodeTheMemoryio:=False;
End;


Begin;
CompileOneLine:=True;
oc:=c;d:='';
a:=getWord(c);
if (a='platform') then goto oke;
if (a='proc') then begin;
  outputLine('','');
  ProcedureSeenName:=getWord(c);
  d:=ProcedureSeenName+' proc';
  goto vege;
  end;
if (a='endp') then begin;
  outputLine(ProcedureSeenName+' endp',oc);
  outputLine('','');
  goto oke;
  end;
if (a='label') then begin;
  d:=getWord(c)+':';
  goto vege;
  end;
if (a='defb') or (a='defw') or (a='defd') or (a='defq') then begin;
  d:='d'+a[4];
  while (c<>'') do d:=d+','+getWord(c);
  d[3]:=' ';
  goto vege;
  end;
if (a='const') then begin;
  d:=getWord(c)+' equ ';
  d:=d+getWord(c);
  goto vege;
  end;
if (a='align') then begin;
  d:=a+' '+getWord(c);
  goto vege;
  end;
if (a='add') or (a='sub') or (a='or') or (a='xor') or (a='and') then begin;
  d:=a+' ';
  a:=getWord(c);
  o:=getSizes(a);
  if (o<0) then exit;
  a:=getWord(c);
  i:=getRegister(a,true,true);
  if (i<0) then exit;
  a:=getWord(c);
  a:=getNumReg(a,o);
  if (a='') then exit;
  d:=d+RegNames[o][i]+','+a;
  goto vege;
  end;
if (a='mul') then begin;
  a:=getWord(c);
  i:=getSigns(a);
  if (i<0) then exit;
  o:=getSizes(a);
  if (o<0) then exit;
  if (o<2) then o:=2;
  if (i=1) then o:=o or $80;
  a:=getWord(c);
  i:=getRegister(a,true,true);
  if (i<0) then exit;
  a:=getWord(c);
  a:=getNumReg(a,o);
  if (a='') then exit;
  d:='imul '+RegNames[o and 15][i]+','+a;
  goto vege;
  end;
if (a='div') then begin;
  if CodeTheDividing(false) then exit;
  goto oke;
  end;
if (a='mod') then begin;
  if CodeTheDividing(true) then exit;
  goto oke;
  end;
if (a='not') or (a='neg') then begin;
  d:=a+' ';
  a:=getWord(c);
  o:=getSizes(a);
  if (o<0) then exit;
  a:=getWord(c);
  i:=getRegister(a,true,true);
  if (i<0) then exit;
  d:=d+RegNames[o][i];
  goto vege;
  end;
if (a='shl') or (a='shr') then begin;
  d:=a+' ';
  a:=getWord(c);
  o:=getSizes(a);
  if (o<0) then exit;
  a:=getWord(c);
  i:=getRegister(a,true,true);
  if (i<0) then exit;
  a:=getWord(c);
  p:=getRegister(a,true,true);
  if (p<0) then begin;
    d:=d+RegNames[o][i]+','+a;
    goto vege;
    end;
  if (p=3) then begin;
    d:=d+RegNames[o][i]+',cl';
    goto vege;
    end;
  if (i=3) then begin;
    outputLine('xchg '+RegNames[o][i]+','+RegNames[o][p],oc);
    outputLine(d+RegNames[o][p]+',cl','');
    outputLine('xchg '+RegNames[o][i]+','+RegNames[o][p],'');
    goto oke;
    end;
  CodePushPop('push',3,1);
  outputLine('mov cl,'+RegNames[1][p],'');
  outputLine(d+RegNames[o][i]+',cl','');
  CodePushPop('pop',3,1);
  goto oke;
  end;
if (a='push') or (a='pop') then begin;
  d:=a;
  a:=getWord(c);
  o:=getSizes(a);
  if (o<0) then exit;
  a:=getWord(c);
  i:=getRegister(a,true,true);
  if (i<0) then exit;
  CodePushPop(d,i,o);
  goto oke;
  end;
if (a='comp') then begin;
  a:=getWord(c);
  i:=getSigns(a);
  if (i<0) then exit;
  o:=getSizes(a);
  if (o<0) then exit;
  CompareSeenSign:=(i=1);
  a:=getWord(c);
  i:=getRegister(a,true,true);
  if (i<0) then exit;
  a:=getWord(c);
  a:=getNumReg(a,o);
  if (a='') then exit;
  d:='cmp '+RegNames[o][i]+','+a;
  goto vege;
  end;
if (a='move') then begin;
  a:=getWord(c);
  sig1:=getSigns(a);
  if (sig1<0) then exit;
  siz1:=getSizes(a);
  if (siz1<0) then exit;
  sig2:=getSigns(a);
  if (sig2<0) then exit;
  siz2:=getSizes(a);
  if (siz2<0) then exit;
  a:=getWord(c);
  reg1:=getRegister(a,true,true);
  if (reg1<0) then exit;
  a:=getWord(c);
  reg2:=getRegister(a,true,true);
  if (reg2<0) then begin;
    d:='mov '+RegNames[siz1][reg1]+','+a;
    goto vege;
    end;
  if (siz1<=siz2) then begin;
    d:='mov '+RegNames[siz1][reg1]+','+RegNames[siz1][reg2];
    goto vege;
    end;
  if (sig1=1) and (sig2=1) then d:='movsx' else d:='movzx';
  d:=d+' '+RegNames[siz1][reg1]+','+RegNames[siz2][reg2];
  goto vege;
  end;
if (a='movr') then begin;
  if CodeTheMemoryio(true) then exit;
  goto oke;
  end;
if (a='movw') then begin;
  if CodeTheMemoryio(false) then exit;
  goto oke;
  end;
if (a='call') then begin;
  a:=getWord(c);
  if (a='') then exit;
  d:='call '+a;
  goto vege;
  end;
if (a='ret') then begin;
  d:='ret';
  goto vege;
  end;
if (a='jump') then begin;
  a:=getWord(c);
  if (a='') then exit;
  d:='jmp '+a;
  goto vege;
  end;
if (a='cllr') then begin;
  o:=getRegister(c,false,true);
  if (o<0) then exit;
  d:='call '+RegNames[3][o];
  goto vege;
  end;
if (a='jmpr') then begin;
  o:=getRegister(c,false,true);
  if (o<0) then exit;
  d:='jmp '+RegNames[3][o];
  goto vege;
  end;
if (a='jmpc') then begin;
  a:=getWord(c);
  i:=getCondition(a);
  if (i<0) then exit;
  a:=getWord(c);
  if (a='') then exit;
  d:='j'+CondCods[CompareSeenSign][i]+' '+a;
  goto vege;
  end;
if (a='setc') then begin;
  a:=getWord(c);
  i:=getCondition(a);
  if (i<0) then exit;
  a:=getWord(c);
  p:=getSizes(a);
  if (p<0) then exit;
  a:=getWord(c);
  o:=getRegister(a,true,true);
  if (o<0) then exit;
  if (p>1) then begin; outputLine('mov '+RegNames[p][o]+',0',oc);oc:=''; end;
  d:='set'+CondCods[CompareSeenSign][i]+' '+RegNames[1][o];
  goto vege;
  end;
if (a='xchg') then begin;
  a:=getWord(c);
  p:=getSizes(a);
  if (p<0) then exit;
  a:=getWord(c);
  d:=getMemory(a);
  if (d='') then exit;
  a:=getWord(c);
  o:=getRegister(a,true,true);
  if (o<0) then exit;
  d:='xchg '+d+','+RegNames[p][o];
  goto vege;
  end;
if (a='addrlod') then begin;
  a:=getWord(c);
  o:=getRegister(a,false,true);
  if (o<0) then exit;
  a:=getWord(c);
  d:=getMemory(a);
  if (d='') then exit;
  d:='mov '+RegNames[3][o]+','+d;
  goto vege;
  end;
if (a='addrsav') then begin;
  a:=getWord(c);
  d:=getMemory(a);
  if (d='') then exit;
  a:=getWord(c);
  o:=getRegister(a,false,true);
  if (o<0) then exit;
  d:='mov '+d+','+RegNames[3][o];
  goto vege;
  end;
if (a='procaddr') then begin;
  a:=getWord(c);
  o:=getRegister(a,false,true);
  if (o<0) then exit;
  d:=getWord(c);
  if (d='') then exit;
  c:=RegNames[3][o];
  if (d='-') then begin;
    outputLine('pop '+c,oc);
    outputLine('push '+c,'');
    end else begin;
    outputLine('mov '+c+',[procTable_data+'+d+'*4]',oc);
    end;
  goto oke;
  end;
if (a='procallocbeg') then begin;
  a:=getWord(c);
  if (a='') then exit;
  d:=getWord(c);
  if (d='') then exit;
  outputLine('push dword ptr [procTable_data+'+a+'*4]',oc);
  outputLine('push dword ptr [procTable_pos]','');
  outputLine('add dword ptr [procTable_pos],'+d,'');
  goto oke;
  end;
if (a='procallocend') then begin;
  a:=getWord(c);
  if (a='') then exit;
  d:=getWord(c);
  if (d='') then exit;
  outputLine('pop dword ptr [procTable_data+'+a+'*4]',oc);
  goto oke;
  end;
if (a='procfree') then begin;
  a:=getWord(c);
  if (a='') then exit;
  d:=getWord(c);
  if (d='') then exit;
  outputLine('pop dword ptr [procTable_data+'+a+'*4]',oc);
  outputLine('sub dword ptr [procTable_pos],'+d,'');
  goto oke;
  end;
if (a='codeofs') then begin;
  a:=getWord(c);
  o:=getRegister(a,false,true);
  if (o<0) then exit;
  d:=getWord(c);
  if (d='') then exit;
  d:='mov '+RegNames[3][o]+',offset '+d;
  goto vege;
  end;
if (a='syscall') then begin;
  d:=c;
  a:=getWord(d);
  if (a='startup') then begin;
    outputLine('program_main:',oc);
    a:=getWord(d);
    outputLine('procTable_max equ '+a,'');
    a:=getWord(d);
    outputLine('program_stack equ '+a,'');
    a:=getWord(d);
    outputLine('program_heap equ '+a,'');
    goto oke;
    end;
  c:='--- '+c;
  xtSetPos(ttbl,0);
  repeat
    if xtEOF(ttbl) then exit;
    a:=xtReadLn(ttbl,255);
    until (a=c);
  d:='autolabel'+BStr(lineNum)+'_';
  a:=';'+oc;
  repeat
    kicserel('@_',d,a);
    xtWriteLn(tout,a);
    if xtEOF(ttbl) then exit;
    a:=xtReadLn(ttbl,255);
    until (a='---');
  goto oke;
  end;

Exit;
vege:
outputLine(d,oc);
oke:
CompileOneLine:=False;
End;


Label f1,f2;
Var
  i:LongInt;
  a,b:String;
BEGIN;
WriteLn('Virtual Machine Compiler v1.0, done by Mc at '#%date' '#%time'.');
WriteLn('platform: dos protected mode with extender (tasm syntax)');
a:=paramStr(1);
b:=paramStr(2);
if (a='') or (b='') then begin;
  WriteLn('using: compiler.code <source> <object> [table]');
  Halt(1);
  end;
if (xtOpen(ttbl,getBaseName,true)<>0) then begin;
  WriteLn('error operning source table!');
  Halt(1);
  end;
if (xtOpen(tinp,a,true)<>0) then begin;
  WriteLn('error opening source file!');
  Halt(2);
  end;
xErase(b);
xCreate(b);
if (xtOpen(tout,b,false)<>0) then begin;
  WriteLn('error opening target file!');
  Halt(2);
  end;
i:=0;
WriteLn('source: '+a);
WriteLn('target: '+b);
WriteLn('compiling...');
CompileOneLine('syscall @compiler prefix-code',-1);
f1:
if xtEOF(tinp) then goto f2;
inc(i);
a:=xtReadLn(tinp,255);
a:=kicsi(a);
if (a='') then goto f1;
if (copy(a,1,1)=';') then begin;
  outputLine(a,'');
  goto f1;
  end;
Write(BStr(i)+#13);
if not CompileOneLine(a,i) then goto f1;
WriteLn('position: '+BStr(i));
WriteLn('contents: "'+a+'"');
WriteLn('invalid instruction, terminating!');
Halt(3);
f2:
CompileOneLine('syscall @compiler postfix-code',-2);
xtClose(tout);
WriteLn('successful!');
END.