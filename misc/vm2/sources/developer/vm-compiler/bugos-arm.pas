{$stack 1k}
{$heap 16k}
{$sysinc system.inc}
{$sysinc param.inc}
{$sysinc alap.inc}
{$sysinc filesys.inc}
{$sysinc filesys2.inc}
{$sysinc textfile.inc}
Const
  regNames:array[1..6] of string[3]=('r6','r7','r8','r9','r10','r11');
  CompareSeenSign:Boolean=true;
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
  'q':i:=4;
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




Function CompileOneLine(c:String;lineNum:LongInt):Boolean;
Label oke,vege;
Const
  byteSizes:array[1..4] of byte=(1,2,4,0);
Var
  siz1,siz2,siz3:integer;
  sig1,sig2,sig3:integer;
  reg1,reg2,reg3:integer;
  par1,par2:String;
  oc,a,b,d:String;
  i,o,p:LongInt;

Procedure CodeOneNum(r,n:String);
Var i,o,p:LongInt;
Begin;
if (n='0') then begin;
  outputLine('mov '+r+',0','');
  exit;
  end;
o:=BVal(n);
if (o=0) then begin;
  outputLine('mov '+r+','+n,'');
  outputLine('orr '+r+','+r+','+n+'!8','');
  outputLine('orr '+r+','+r+','+n+'!16','');
  outputLine('orr '+r+','+r+','+n+'!24','');
  exit;
  end;
n:='mov ';
p:=-8;
for i:=0 to 3 do begin;
  inc(p,8);
  if ((o shr p) and $ff=0) then continue;
  outputLine(n+r+','+BStr(o)+'!'+BStr(p),'');
  n:='orr '+r+',';
  end;
End;

Procedure CodeOnePush(r:String);
Begin;
outputLine('sub sp,sp,4','push '+r);
outputLine('str '+r+',[sp]','');
End;

Procedure CodeOnePop(r:String);
Begin;
outputLine('ldr '+r+',[sp]','pop '+r);
outputLine('add sp,sp,4','');
End;

Function CodeOneMem(a,t:string):String;
Var i,o:longint;
Begin;
CodeOneMem:='';
if (copy(a,1,1)<>'[') then exit;
if (copy(a,length(a),1)<>']') then exit;
a:=copy(a,2,length(a)-2);
if (length(a)=3) then a:=a+'+0';
o:=getRegister(copy(a,1,3),false,true);
if (o<1) then exit;
a:=copy(a,4,$666);
if (a='+0') or (a='-0') then a:='0';
i:=BVal(a);
if (i=0) then begin;
  CodeOneMem:='['+regNames[o]+']';
  exit;
  end;
if (abs(i) and $ff=abs(i)) then begin;
  a:=BStr(i);
  if (i>0) then a:='+'+a;
  CodeOneMem:='['+regNames[o]+a+']';
  exit;
  end;
CodeOneNum(t,a);
CodeOneMem:='['+regNames[o]+'+'+t+']';
End;

{
Procedure CodeBitExtend(dst,src:String;sig,siz:LongInt);
Const
  signDat:array[1..2] of String[3]=('s','u');
  sizeDat:array[1..2] of String[3]=('b','h');
Begin;
if (siz in [1..2]) then begin;
  if not (sig in [1..2]) then sig:=2;
  outputLine(signDat[sig]+'xt'+sizeDat[siz]+' '+dst+','+src,'');
  exit;
  end;
if (src=dst) then exit;
outputLine('mov '+dst+','+src,'');
End;
}

Procedure CodeBitExtend(dst,src:String;sig,siz:LongInt);
Begin;
if not (siz in [1..2]) then begin;
  if (src=dst) then exit;
  outputLine('mov '+dst+','+src,'');
  exit;
  end;
if (siz=1) then outputLine('and '+dst+','+src+',0ffh','') else begin;
  outputLine('bic '+dst+','+src+',0ff000000h!24','');
  outputLine('bic '+dst+','+dst+',000ff0000h!16','');
  end;
if (sig=2) then exit;
if (siz=2) then src:='8000h!8' else src:='80h';
outputLine('tst '+dst+','+src,'');
outputLine('orrne '+dst+','+dst+',0ff000000h!24','');
outputLine('orrne '+dst+','+dst+',000ff0000h!16','');
if (siz=1) then outputLine('orrne '+dst+','+dst+',00000ff00h!8','');
End;

{
Procedure CodeMSBconvert(dst,src:String;siz:LongInt);
Begin;
case siz of
  2:outputLine('rev16 '+dst+','+src,'');
  3:outputLine('rev '+dst+','+src,'');
  else if (dst<>src) then outputLine('mov '+dst+','+src,'');
  end;
End;
}

Procedure CodeMSBconvert(dst,src:String;siz:LongInt);

procedure code16(dst,src:String);
begin;
outputLine('and r0,'+src+',0ffh','');
outputLine('mov '+dst+','+src+' lsr 8','');
outputLine('and '+dst+','+dst+',0ffh','');
outputLine('orr '+dst+','+dst+',r0 lsl 8','');
end;

Begin;
case siz of
  2:code16(dst,src);
  3:begin;
    outputLine('mov r1,'+src+' lsr 16','');
    code16('r1','r1');
    code16(dst,src);
    outputLine('orr '+dst+',r1,'+dst+' lsl 16','');
    end;
  else if (dst<>src) then outputLine('mov '+dst+','+src,'');
  end;
End;

Function codeCondition(i:LongInt):String;
Var a:String;
Begin;
codeCondition:='';
b:='';
case i of
  3:a:='ne';                  {ne}
  4:a:='eq';                  {e}
  end;
if CompareSeenSign then begin;
  case i of
    1:a:='gt';                  {a}
    2:a:='lt';                  {b}
    5:a:='ge';                  {ae}
    6:a:='le';                  {be}
    end;
  end else begin;
  case i of
    1:a:='hi';                  {a}
    2:a:='lo';                  {b}
    5:a:='hs';                  {ae}
    6:a:='ls';                  {be}
    end;
  end;
codeCondition:=a;
End;



Begin;
CompileOneLine:=True;
oc:=c;d:='';
a:=getWord(c);
if (a='platform') then goto oke;
if (a='proc') then begin;
  outputLine('','');
  d:='proc '+getWord(c);
  goto vege;
  end;
if (a='endp') then begin;
  outputLine('endp',oc);
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
  if (a='or') then a:='orr';
  if (a='xor') then a:='eor';
  d:=a+' ';
  a:=getWord(c);
  o:=getSizes(a);
  if (o<0) then exit;
  a:=getWord(c);
  p:=getRegister(a,true,true);
  if (p<0) then exit;
  a:=getWord(c);
  i:=getRegister(a,true,true);
  if (i>0) then b:=regNames[i] else begin;
    b:='r0';
    CodeOneNum(b,a);
    end;
  a:=regNames[p];
  d:=d+a+','+a+','+b;
  goto vege;
  end;
if (a='mul') then begin;
  a:=getWord(c);
  o:=getSigns(a);
  if (o<0) then exit;
  o:=getSizes(a);
  if (o<0) then exit;
  a:=getWord(c);
  p:=getRegister(a,true,true);
  if (p<0) then exit;
  a:=getWord(c);
  i:=getRegister(a,true,true);
  if (i>0) then b:=regNames[i] else begin;
    b:='r0';
    CodeOneNum(b,a);
    end;
  a:=regNames[p];
  d:='mul '+a+','+a+','+b;
  goto vege;
  end;
if (a='div') or (a='mod') then begin;
  CodeOnePush('lr');
  if (a='div') then d:='r3' else d:='r2';
  a:=getWord(c);
  case getSigns(a) of
    1:a:='div32s';
    2:a:='div32u';
    else exit;
    end;
  d:=d+a;
  o:=getSizes(a);
  if (o<0) then exit;
  a:=getWord(c);
  p:=getRegister(a,true,true);
  if (p<0) then exit;
  a:=getWord(c);
  i:=getRegister(a,true,true);
  if (i>0) then outputLine('mov r1,'+regNames[i],'') else CodeOneNum('r1',a);
  a:=regNames[p];
  outputLine('mov r0,'+a,oc);
  outputLine('bl '+copy(d,3,666),'');
  outputLine('mov '+a+','+copy(d,1,2),'');
  CodeOnePop('lr');
  goto oke;
  end;
if (a='neg') then begin;
  a:=getWord(c);
  o:=getSizes(a);
  if (o<0) then exit;
  a:=getWord(c);
  p:=getRegister(a,true,true);
  if (p<0) then exit;
  a:=regNames[p];
  d:='rsb '+a+','+a+',0';
  goto vege;
  end;
if (a='not') then begin;
  a:=getWord(c);
  o:=getSizes(a);
  if (o<0) then exit;
  a:=getWord(c);
  p:=getRegister(a,true,true);
  if (p<0) then exit;
  a:=regNames[p];
  d:='mvn '+a+','+a;
  goto vege;
  end;
if (a='shl') or (a='shr') then begin;
  if (a='shl') then d:=' lsl ' else d:=' lsr ';
  a:=getWord(c);
  o:=getSizes(a);
  if (o<0) then exit;
  a:=getWord(c);
  p:=getRegister(a,true,true);
  if (p<0) then exit;
  a:=getWord(c);
  i:=getRegister(a,true,true);
  if (i>0) then b:=regNames[i] else begin;
    b:='r0';
    CodeOneNum(b,a);
    end;
  a:=regNames[p];
  d:='mov '+a+','+a+d+b;
  goto vege;
  end;
if (a='push') or (a='pop') then begin;
  d:=a;
  a:=getWord(c);
  o:=getSizes(a);
  if (o<0) then exit;
  a:=getWord(c);
  i:=getRegister(a,true,true);
  if (i<0) then exit;
  a:=regNames[i];
  if (d='push') then CodeOnePush(a) else CodeOnePop(a);
  goto oke;
  end;
if (a='comp') then begin;
  a:=getWord(c);
  p:=getSigns(a);
  if (p<0) then exit;
  CompareSeenSign:=(p=1);
  o:=getSizes(a);
  if (o<0) then exit;
  a:=getWord(c);
  i:=getRegister(a,true,true);
  if (i<0) then exit;
  CodeBitExtend('r0',regNames[i],p,o);
  a:=getWord(c);
  i:=getRegister(a,true,true);
  if (i>0) then CodeBitExtend('r1',regNames[i],p,o) else CodeOneNum('r1',a);
  d:='cmp r0,r1';
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
    CodeOneNum(regNames[reg1],a);
    goto oke;
    end;
  if (siz1<=siz2) then begin;
    d:='mov '+regNames[reg1]+','+regNames[reg2];
    goto vege;
    end;
  CodeBitExtend(regNames[reg1],regNames[reg2],sig2,siz2);
  goto oke;
  end;
if (a='movr') then begin;
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
  if (siz2>siz1) then siz2:=siz1;
  reg1:=getRegister(par1,true,true);
  if (reg1<0) then exit;
  par1:=regNames[reg1];
  par2:=CodeOneMem(par2,'r0');
  if (par2='') then exit;
  case siz2 of
    1:a:='ldrb';
    2:a:='ldrh';
    3:a:='ldr';
    end;
  outputLine(a+' '+par1+','+par2,oc);
  if (reg3=2) then CodeMSBconvert(par1,par1,siz2);
  if (siz2<siz1) then CodeBitExtend(par1,par1,sig2,siz2);
  goto oke;
  end;
if (a='movw') then begin;
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
  if (siz2>siz1) then siz2:=siz1;
  reg2:=getRegister(par2,true,true);
  if (reg2<0) then exit;
  par2:=regNames[reg2];
  if (siz2<siz1) then begin;
    CodeBitExtend('r2',par2,sig2,siz2);
    par2:='r2';
    end;
  if (reg3=2) then begin;
    CodeMSBconvert('r2',par2,siz1);
    par2:='r2';
    end;
  case siz2 of
    1:a:='strb';
    2:a:='strh';
    3:a:='str';
    end;
  par1:=CodeOneMem(par1,'r0');
  if (par1='') then exit;
  outputLine(a+' '+par2+','+par1,oc);
  goto oke;
  end;
if (a='call') then begin;
  a:=getWord(c);
  if (a='') then exit;
  CodeOnePush('lr');
  outputLine('bl offset '+a,oc);
  CodeOnePop('lr');
  goto oke;
  end;
if (a='ret') then begin;
  outputLine('bx lr',oc);
  goto oke;
  end;
if (a='jump') then begin;
  a:=getWord(c);
  if (a='') then exit;
  outputLine('b offset '+a,oc);
  goto oke;
  end;
if (a='cllr') then begin;
  o:=getRegister(c,false,true);
  if (o<0) then exit;
  CodeOnePush('lr');
  outputLine('blx '+regNames[o],oc);
  CodeOnePop('lr');
  goto oke;
  end;
if (a='jmpr') then begin;
  o:=getRegister(c,false,true);
  if (o<0) then exit;
  outputLine('bx '+regNames[o],oc);
  goto oke;
  end;
if (a='jmpc') then begin;
  a:=getWord(c);
  i:=getCondition(a);
  if (i<0) then exit;
  a:=getWord(c);
  if (a='') then exit;
  b:=codeCondition(i);
  if (b='') then exit;
  outputLine('b'+b+' offset '+a,oc);
  goto oke;
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
  a:=regNames[o];
  b:=codeCondition(i);
  if (b='') then exit;
  outputLine('mov '+a+',0',oc);
  outputLine('mov'+b+' '+a+',1','');
  goto oke;
  end;
if (a='xchg') then begin;
  a:=getWord(c);
  p:=getSizes(a);
  if (p<0) then exit;
  a:=getWord(c);
  b:=CodeOneMem(a,'r0');
  if (b='') then exit;
  a:=getWord(c);
  o:=getRegister(a,true,true);
  if (o<0) then exit;
  case p of
    1:c:='rb';
    2:c:='rh';
    3:c:='r';
    else exit;
    end;
  a:=regNames[o];
  outputLine('mov r1,'+a,oc);
  outputLine('ld'+c+' '+a+','+b,'');
  outputLine('st'+c+' r1,'+b,'');
  goto oke;
  end;
if (a='addrlod') then begin;
  a:=getWord(c);
  o:=getRegister(a,false,true);
  if (o<0) then exit;
  a:=getWord(c);
  d:=CodeOneMem(a,'r0');
  if (d='') then exit;
  d:='ldr '+RegNames[o]+','+d;
  goto vege;
  end;
if (a='addrsav') then begin;
  a:=getWord(c);
  d:=CodeOneMem(a,'r0');
  if (d='') then exit;
  a:=getWord(c);
  o:=getRegister(a,false,true);
  if (o<0) then exit;
  d:='str '+RegNames[o]+','+d;
  goto vege;
  end;
if (a='procaddr') then begin;
  a:=getWord(c);
  o:=getRegister(a,false,true);
  if (o<0) then exit;
  d:=getWord(c);
  if (d='') then exit;
  c:=regNames[o];
  if (d='-') then begin;
    outputLine('mov '+c+',r5',oc);
    end else begin;
    CodeOneNum('r0',d);
    CodeOneNum('r1','offset lastbyte');
    outputLine('ldr '+c+',[r1+r0 lsl 2]',oc);
    end;
  goto oke;
  end;
if (a='procallocbeg') then begin;
  a:=getWord(c);
  if (a='') then exit;
  d:=getWord(c);
  if (d='') then exit;
  CodeOneNum('r0',a);
  CodeOneNum('r1','offset lastbyte');
  outputLine('ldr r1,[r1+r0 lsl 2]',oc);
  CodeOnePush('r1');
  CodeOnePush('r5');
  outputLine('mov r5,r12','');
  CodeOneNum('r0',d);
  outputLine('add r12,r12,r0','');
  goto oke;
  end;
if (a='procallocend') then begin;
  a:=getWord(c);
  if (a='') then exit;
  d:=getWord(c);
  if (d='') then exit;
  CodeOneNum('r0',a);
  CodeOneNum('r1','offset lastbyte');
  outputLine('str r5,[r1+r0 lsl 2]',oc);
  CodeOnePop('r5');
  goto oke;
  end;
if (a='procfree') then begin;
  a:=getWord(c);
  if (a='') then exit;
  d:=getWord(c);
  if (d='') then exit;
  CodeOneNum('r0',a);
  CodeOneNum('r1','offset lastbyte');
  CodeOnePop('r2');
  outputLine('str r2,[r1+r0 lsl 2]',oc);
  CodeOneNum('r0',d);
  outputLine('sub r12,r12,r0','');
  goto oke;
  end;
if (a='codeofs') then begin;
  a:=getWord(c);
  o:=getRegister(a,false,true);
  if (o<0) then exit;
  a:=getWord(c);
  if (a='') then exit;
  CodeOneNum(regNames[o],'offset '+a);
  goto oke;
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
WriteLn('platform: BugOS ARM (sasm syntax)');
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