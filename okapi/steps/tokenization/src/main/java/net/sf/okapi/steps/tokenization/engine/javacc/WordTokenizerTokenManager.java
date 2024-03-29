/* Generated By:JavaCC: Do not edit this line. WordTokenizerTokenManager.java */
package net.sf.okapi.steps.tokenization.engine.javacc;
import java.io.*;

/** Token Manager. */
public class WordTokenizerTokenManager implements WordTokenizerConstants
{

  /** Debug output. */
  public  java.io.PrintStream debugStream = System.out;
  /** Set debug output. */
  public  void setDebugStream(java.io.PrintStream ds) { debugStream = ds; }
private int jjMoveStringLiteralDfa0_0()
{
   return jjMoveNfa_0(0, 0);
}
static final long[] jjbitVec0 = {
   0x1600L, 0x0L, 0x0L, 0x0L
};
static final long[] jjbitVec1 = {
   0x0L, 0xffc000000000L, 0x0L, 0xffc000000000L
};
static final long[] jjbitVec2 = {
   0x0L, 0x3ff00000000L, 0x0L, 0x3ff000000000000L
};
static final long[] jjbitVec3 = {
   0x0L, 0xffc000000000L, 0x0L, 0xff8000000000L
};
static final long[] jjbitVec4 = {
   0x0L, 0xffc000000000L, 0x0L, 0x0L
};
static final long[] jjbitVec5 = {
   0x0L, 0x3ff0000L, 0x0L, 0x3ff0000L
};
static final long[] jjbitVec6 = {
   0x0L, 0x3ffL, 0x0L, 0x0L
};
static final long[] jjbitVec7 = {
   0xfffffffeL, 0x0L, 0x0L, 0x0L
};
static final long[] jjbitVec9 = {
   0x0L, 0x0L, 0x0L, 0xff7fffffff7fffffL
};
private int jjMoveNfa_0(int startState, int curPos)
{
   int startsAt = 0;
   jjnewStateCnt = 75;
   int i = 1;
   jjstateSet[0] = startState;
   int kind = 0x7fffffff;
   for (;;)
   {
      if (++jjround == 0x7fffffff)
         ReInitRounds();
      if (curChar < 64)
      {
         long l = 1L << curChar;
         do
         {
            switch(jjstateSet[--i])
            {
               case 0:
                  if ((0x3ff000000000000L & l) != 0L)
                  {
                     if (kind > 2)
                        kind = 2;
                     jjCheckNAddStates(0, 17);
                  }
                  if ((0x3ff000000000000L & l) != 0L)
                     jjCheckNAddStates(18, 23);
                  break;
               case 1:
               case 52:
                  if ((0x3ff000000000000L & l) != 0L)
                     jjCheckNAddTwoStates(1, 2);
                  break;
               case 2:
                  if ((0xf00000000000L & l) != 0L)
                     jjCheckNAdd(3);
                  break;
               case 3:
                  if ((0x3ff000000000000L & l) == 0L)
                     break;
                  if (kind > 8)
                     kind = 8;
                  jjCheckNAdd(3);
                  break;
               case 4:
               case 61:
                  if ((0x3ff000000000000L & l) != 0L)
                     jjCheckNAddTwoStates(4, 5);
                  break;
               case 5:
                  if ((0xf00000000000L & l) != 0L)
                     jjCheckNAdd(6);
                  break;
               case 6:
                  if ((0x3ff000000000000L & l) != 0L)
                     jjCheckNAddTwoStates(6, 7);
                  break;
               case 7:
                  if ((0xf00000000000L & l) != 0L)
                     jjCheckNAddTwoStates(8, 9);
                  break;
               case 8:
                  if ((0x3ff000000000000L & l) != 0L)
                     jjCheckNAddTwoStates(8, 9);
                  break;
               case 9:
               case 10:
                  if ((0x3ff000000000000L & l) == 0L)
                     break;
                  if (kind > 8)
                     kind = 8;
                  jjCheckNAddTwoStates(5, 10);
                  break;
               case 11:
               case 74:
                  if ((0x3ff000000000000L & l) != 0L)
                     jjCheckNAddTwoStates(11, 12);
                  break;
               case 12:
                  if ((0xf00000000000L & l) != 0L)
                     jjCheckNAdd(13);
                  break;
               case 13:
                  if ((0x3ff000000000000L & l) != 0L)
                     jjCheckNAddTwoStates(13, 14);
                  break;
               case 14:
                  if ((0xf00000000000L & l) != 0L)
                     jjCheckNAddTwoStates(15, 16);
                  break;
               case 15:
                  if ((0x3ff000000000000L & l) != 0L)
                     jjCheckNAddTwoStates(15, 16);
                  break;
               case 16:
               case 17:
                  if ((0x3ff000000000000L & l) != 0L)
                     jjCheckNAddTwoStates(17, 18);
                  break;
               case 18:
                  if ((0xf00000000000L & l) != 0L)
                     jjCheckNAdd(19);
                  break;
               case 19:
                  if ((0x3ff000000000000L & l) == 0L)
                     break;
                  if (kind > 8)
                     kind = 8;
                  jjCheckNAddTwoStates(14, 19);
                  break;
               case 22:
                  if (curChar == 45)
                     jjstateSet[jjnewStateCnt++] = 23;
                  break;
               case 25:
                  if (curChar == 39)
                     jjstateSet[jjnewStateCnt++] = 26;
                  break;
               case 28:
                  if (curChar == 46)
                     jjCheckNAdd(29);
                  break;
               case 30:
                  if (curChar != 46)
                     break;
                  if (kind > 4)
                     kind = 4;
                  jjCheckNAdd(29);
                  break;
               case 32:
                  if (curChar == 38)
                     jjstateSet[jjnewStateCnt++] = 33;
                  break;
               case 34:
                  if ((0x3ff000000000000L & l) == 0L)
                     break;
                  if (kind > 2)
                     kind = 2;
                  jjCheckNAddStates(0, 17);
                  break;
               case 35:
                  if ((0x3ff000000000000L & l) == 0L)
                     break;
                  if (kind > 2)
                     kind = 2;
                  jjCheckNAdd(35);
                  break;
               case 36:
                  if ((0x3ff000000000000L & l) != 0L)
                     jjCheckNAddStates(24, 26);
                  break;
               case 37:
                  if ((0x600000000000L & l) != 0L)
                     jjCheckNAdd(38);
                  break;
               case 38:
                  if ((0x3ff000000000000L & l) != 0L)
                     jjCheckNAddStates(27, 29);
                  break;
               case 40:
                  if ((0x3ff000000000000L & l) != 0L)
                     jjCheckNAddTwoStates(40, 41);
                  break;
               case 41:
                  if ((0x600000000000L & l) != 0L)
                     jjCheckNAdd(42);
                  break;
               case 42:
                  if ((0x3ff000000000000L & l) == 0L)
                     break;
                  if (kind > 6)
                     kind = 6;
                  jjCheckNAddTwoStates(41, 42);
                  break;
               case 43:
                  if ((0x3ff000000000000L & l) != 0L)
                     jjCheckNAddTwoStates(43, 44);
                  break;
               case 44:
                  if (curChar == 46)
                     jjCheckNAdd(45);
                  break;
               case 45:
                  if ((0x3ff000000000000L & l) == 0L)
                     break;
                  if (kind > 7)
                     kind = 7;
                  jjCheckNAddTwoStates(44, 45);
                  break;
               case 46:
                  if ((0x3ff000000000000L & l) != 0L)
                     jjCheckNAddTwoStates(46, 47);
                  break;
               case 47:
                  if ((0xf00000000000L & l) != 0L)
                     jjCheckNAddTwoStates(48, 49);
                  break;
               case 48:
                  if ((0x3ff000000000000L & l) != 0L)
                     jjCheckNAddTwoStates(48, 49);
                  break;
               case 49:
               case 50:
                  if ((0x3ff000000000000L & l) == 0L)
                     break;
                  if (kind > 8)
                     kind = 8;
                  jjCheckNAdd(50);
                  break;
               case 51:
                  if ((0x3ff000000000000L & l) != 0L)
                     jjCheckNAddTwoStates(51, 52);
                  break;
               case 53:
                  if ((0x3ff000000000000L & l) != 0L)
                     jjCheckNAddTwoStates(53, 54);
                  break;
               case 54:
                  if ((0xf00000000000L & l) != 0L)
                     jjCheckNAddTwoStates(55, 56);
                  break;
               case 55:
                  if ((0x3ff000000000000L & l) != 0L)
                     jjCheckNAddTwoStates(55, 56);
                  break;
               case 56:
               case 57:
                  if ((0x3ff000000000000L & l) != 0L)
                     jjCheckNAddTwoStates(57, 58);
                  break;
               case 58:
                  if ((0xf00000000000L & l) != 0L)
                     jjCheckNAdd(59);
                  break;
               case 59:
                  if ((0x3ff000000000000L & l) == 0L)
                     break;
                  if (kind > 8)
                     kind = 8;
                  jjCheckNAddTwoStates(54, 59);
                  break;
               case 60:
                  if ((0x3ff000000000000L & l) != 0L)
                     jjCheckNAddTwoStates(60, 61);
                  break;
               case 62:
                  if ((0x3ff000000000000L & l) != 0L)
                     jjCheckNAddTwoStates(62, 63);
                  break;
               case 63:
                  if ((0xf00000000000L & l) != 0L)
                     jjCheckNAddTwoStates(64, 65);
                  break;
               case 64:
                  if ((0x3ff000000000000L & l) != 0L)
                     jjCheckNAddTwoStates(64, 65);
                  break;
               case 65:
               case 66:
                  if ((0x3ff000000000000L & l) != 0L)
                     jjCheckNAddTwoStates(66, 67);
                  break;
               case 67:
                  if ((0xf00000000000L & l) != 0L)
                     jjCheckNAdd(68);
                  break;
               case 68:
                  if ((0x3ff000000000000L & l) != 0L)
                     jjCheckNAddTwoStates(68, 69);
                  break;
               case 69:
                  if ((0xf00000000000L & l) != 0L)
                     jjCheckNAddTwoStates(70, 71);
                  break;
               case 70:
                  if ((0x3ff000000000000L & l) != 0L)
                     jjCheckNAddTwoStates(70, 71);
                  break;
               case 71:
               case 72:
                  if ((0x3ff000000000000L & l) == 0L)
                     break;
                  if (kind > 8)
                     kind = 8;
                  jjCheckNAddTwoStates(67, 72);
                  break;
               case 73:
                  if ((0x3ff000000000000L & l) != 0L)
                     jjCheckNAddTwoStates(73, 74);
                  break;
               default : break;
            }
         } while(i != startsAt);
      }
      else if (curChar < 128)
      {
         long l = 1L << (curChar & 077);
         do
         {
            switch(jjstateSet[--i])
            {
               case 0:
                  if ((0x7fffffe07fffffeL & l) != 0L)
                  {
                     if (kind > 2)
                        kind = 2;
                     jjCheckNAddStates(0, 17);
                  }
                  if ((0x7fffffe07fffffeL & l) != 0L)
                     jjCheckNAddStates(30, 37);
                  break;
               case 1:
                  if ((0x7fffffe07fffffeL & l) != 0L)
                     jjAddStates(38, 39);
                  break;
               case 2:
                  if (curChar == 95)
                     jjCheckNAdd(3);
                  break;
               case 3:
                  if ((0x7fffffe07fffffeL & l) == 0L)
                     break;
                  if (kind > 8)
                     kind = 8;
                  jjCheckNAdd(3);
                  break;
               case 4:
                  if ((0x7fffffe07fffffeL & l) != 0L)
                     jjCheckNAddTwoStates(4, 5);
                  break;
               case 5:
                  if (curChar == 95)
                     jjCheckNAdd(6);
                  break;
               case 6:
                  if ((0x7fffffe07fffffeL & l) != 0L)
                     jjCheckNAddTwoStates(6, 7);
                  break;
               case 7:
                  if (curChar == 95)
                     jjCheckNAddTwoStates(8, 9);
                  break;
               case 8:
                  if ((0x7fffffe07fffffeL & l) != 0L)
                     jjCheckNAddTwoStates(8, 9);
                  break;
               case 10:
                  if ((0x7fffffe07fffffeL & l) == 0L)
                     break;
                  if (kind > 8)
                     kind = 8;
                  jjCheckNAddTwoStates(5, 10);
                  break;
               case 11:
                  if ((0x7fffffe07fffffeL & l) != 0L)
                     jjAddStates(40, 41);
                  break;
               case 12:
                  if (curChar == 95)
                     jjCheckNAdd(13);
                  break;
               case 13:
                  if ((0x7fffffe07fffffeL & l) != 0L)
                     jjCheckNAddTwoStates(13, 14);
                  break;
               case 14:
                  if (curChar == 95)
                     jjCheckNAddTwoStates(15, 16);
                  break;
               case 15:
                  if ((0x7fffffe07fffffeL & l) != 0L)
                     jjCheckNAddTwoStates(15, 16);
                  break;
               case 17:
                  if ((0x7fffffe07fffffeL & l) != 0L)
                     jjAddStates(42, 43);
                  break;
               case 18:
                  if (curChar == 95)
                     jjCheckNAdd(19);
                  break;
               case 19:
                  if ((0x7fffffe07fffffeL & l) == 0L)
                     break;
                  if (kind > 8)
                     kind = 8;
                  jjCheckNAddTwoStates(14, 19);
                  break;
               case 20:
                  if ((0x7fffffe07fffffeL & l) != 0L)
                     jjCheckNAddStates(30, 37);
                  break;
               case 21:
                  if ((0x7fffffe07fffffeL & l) != 0L)
                     jjCheckNAddTwoStates(21, 22);
                  break;
               case 23:
                  if ((0x7fffffe07fffffeL & l) == 0L)
                     break;
                  if (kind > 1)
                     kind = 1;
                  jjstateSet[jjnewStateCnt++] = 23;
                  break;
               case 24:
                  if ((0x7fffffe07fffffeL & l) != 0L)
                     jjCheckNAddTwoStates(24, 25);
                  break;
               case 26:
                  if ((0x7fffffe07fffffeL & l) == 0L)
                     break;
                  if (kind > 3)
                     kind = 3;
                  jjCheckNAddTwoStates(25, 26);
                  break;
               case 27:
                  if ((0x7fffffe07fffffeL & l) != 0L)
                     jjCheckNAddTwoStates(27, 28);
                  break;
               case 29:
                  if ((0x7fffffe07fffffeL & l) != 0L)
                     jjAddStates(44, 45);
                  break;
               case 31:
                  if ((0x7fffffe07fffffeL & l) != 0L)
                     jjCheckNAddTwoStates(31, 32);
                  break;
               case 32:
                  if (curChar == 64)
                     jjCheckNAdd(33);
                  break;
               case 33:
                  if ((0x7fffffe07fffffeL & l) == 0L)
                     break;
                  if (kind > 5)
                     kind = 5;
                  jjCheckNAdd(33);
                  break;
               case 34:
                  if ((0x7fffffe07fffffeL & l) == 0L)
                     break;
                  if (kind > 2)
                     kind = 2;
                  jjCheckNAddStates(0, 17);
                  break;
               case 35:
                  if ((0x7fffffe07fffffeL & l) == 0L)
                     break;
                  if (kind > 2)
                     kind = 2;
                  jjCheckNAdd(35);
                  break;
               case 36:
                  if ((0x7fffffe07fffffeL & l) != 0L)
                     jjCheckNAddStates(24, 26);
                  break;
               case 37:
                  if (curChar == 95)
                     jjCheckNAdd(38);
                  break;
               case 38:
                  if ((0x7fffffe07fffffeL & l) != 0L)
                     jjCheckNAddStates(27, 29);
                  break;
               case 39:
                  if (curChar == 64)
                     jjCheckNAdd(40);
                  break;
               case 40:
                  if ((0x7fffffe07fffffeL & l) != 0L)
                     jjCheckNAddTwoStates(40, 41);
                  break;
               case 42:
                  if ((0x7fffffe07fffffeL & l) == 0L)
                     break;
                  if (kind > 6)
                     kind = 6;
                  jjCheckNAddTwoStates(41, 42);
                  break;
               case 43:
                  if ((0x7fffffe07fffffeL & l) != 0L)
                     jjCheckNAddTwoStates(43, 44);
                  break;
               case 45:
                  if ((0x7fffffe07fffffeL & l) == 0L)
                     break;
                  if (kind > 7)
                     kind = 7;
                  jjCheckNAddTwoStates(44, 45);
                  break;
               case 46:
                  if ((0x7fffffe07fffffeL & l) != 0L)
                     jjCheckNAddTwoStates(46, 47);
                  break;
               case 47:
                  if (curChar == 95)
                     jjCheckNAddTwoStates(48, 49);
                  break;
               case 48:
                  if ((0x7fffffe07fffffeL & l) != 0L)
                     jjCheckNAddTwoStates(48, 49);
                  break;
               case 50:
                  if ((0x7fffffe07fffffeL & l) == 0L)
                     break;
                  if (kind > 8)
                     kind = 8;
                  jjstateSet[jjnewStateCnt++] = 50;
                  break;
               case 51:
                  if ((0x7fffffe07fffffeL & l) != 0L)
                     jjCheckNAddTwoStates(51, 52);
                  break;
               case 53:
                  if ((0x7fffffe07fffffeL & l) != 0L)
                     jjCheckNAddTwoStates(53, 54);
                  break;
               case 54:
                  if (curChar == 95)
                     jjCheckNAddTwoStates(55, 56);
                  break;
               case 55:
                  if ((0x7fffffe07fffffeL & l) != 0L)
                     jjCheckNAddTwoStates(55, 56);
                  break;
               case 57:
                  if ((0x7fffffe07fffffeL & l) != 0L)
                     jjAddStates(46, 47);
                  break;
               case 58:
                  if (curChar == 95)
                     jjCheckNAdd(59);
                  break;
               case 59:
                  if ((0x7fffffe07fffffeL & l) == 0L)
                     break;
                  if (kind > 8)
                     kind = 8;
                  jjCheckNAddTwoStates(54, 59);
                  break;
               case 60:
                  if ((0x7fffffe07fffffeL & l) != 0L)
                     jjCheckNAddTwoStates(60, 61);
                  break;
               case 62:
                  if ((0x7fffffe07fffffeL & l) != 0L)
                     jjCheckNAddTwoStates(62, 63);
                  break;
               case 63:
                  if (curChar == 95)
                     jjCheckNAddTwoStates(64, 65);
                  break;
               case 64:
                  if ((0x7fffffe07fffffeL & l) != 0L)
                     jjCheckNAddTwoStates(64, 65);
                  break;
               case 66:
                  if ((0x7fffffe07fffffeL & l) != 0L)
                     jjCheckNAddTwoStates(66, 67);
                  break;
               case 67:
                  if (curChar == 95)
                     jjCheckNAdd(68);
                  break;
               case 68:
                  if ((0x7fffffe07fffffeL & l) != 0L)
                     jjCheckNAddTwoStates(68, 69);
                  break;
               case 69:
                  if (curChar == 95)
                     jjCheckNAddTwoStates(70, 71);
                  break;
               case 70:
                  if ((0x7fffffe07fffffeL & l) != 0L)
                     jjCheckNAddTwoStates(70, 71);
                  break;
               case 72:
                  if ((0x7fffffe07fffffeL & l) == 0L)
                     break;
                  if (kind > 8)
                     kind = 8;
                  jjCheckNAddTwoStates(67, 72);
                  break;
               case 73:
                  if ((0x7fffffe07fffffeL & l) != 0L)
                     jjCheckNAddTwoStates(73, 74);
                  break;
               default : break;
            }
         } while(i != startsAt);
      }
      else
      {
         int hiByte = (int)(curChar >> 8);
         int i1 = hiByte >> 6;
         long l1 = 1L << (hiByte & 077);
         int i2 = (curChar & 0xff) >> 6;
         long l2 = 1L << (curChar & 077);
         do
         {
            switch(jjstateSet[--i])
            {
               case 0:
                  if (jjCanMove_0(hiByte, i1, i2, l1, l2))
                     jjCheckNAddStates(18, 23);
                  if (jjCanMove_1(hiByte, i1, i2, l1, l2))
                     jjCheckNAddStates(30, 37);
                  if (jjCanMove_1(hiByte, i1, i2, l1, l2))
                  {
                     if (kind > 2)
                        kind = 2;
                     jjCheckNAddStates(0, 17);
                  }
                  break;
               case 1:
                  if (jjCanMove_1(hiByte, i1, i2, l1, l2))
                     jjCheckNAddTwoStates(1, 2);
                  break;
               case 3:
                  if (!jjCanMove_1(hiByte, i1, i2, l1, l2))
                     break;
                  if (kind > 8)
                     kind = 8;
                  jjstateSet[jjnewStateCnt++] = 3;
                  break;
               case 4:
                  if (jjCanMove_1(hiByte, i1, i2, l1, l2))
                     jjCheckNAddTwoStates(4, 5);
                  break;
               case 6:
                  if (jjCanMove_1(hiByte, i1, i2, l1, l2))
                     jjAddStates(48, 49);
                  break;
               case 8:
                  if (jjCanMove_1(hiByte, i1, i2, l1, l2))
                     jjAddStates(50, 51);
                  break;
               case 9:
                  if (!jjCanMove_0(hiByte, i1, i2, l1, l2))
                     break;
                  if (kind > 8)
                     kind = 8;
                  jjCheckNAddTwoStates(5, 10);
                  break;
               case 10:
                  if (!jjCanMove_1(hiByte, i1, i2, l1, l2))
                     break;
                  if (kind > 8)
                     kind = 8;
                  jjCheckNAddTwoStates(5, 10);
                  break;
               case 11:
                  if (jjCanMove_1(hiByte, i1, i2, l1, l2))
                     jjCheckNAddTwoStates(11, 12);
                  break;
               case 13:
                  if (jjCanMove_1(hiByte, i1, i2, l1, l2))
                     jjCheckNAddTwoStates(13, 14);
                  break;
               case 15:
                  if (jjCanMove_1(hiByte, i1, i2, l1, l2))
                     jjAddStates(52, 53);
                  break;
               case 16:
                  if (jjCanMove_0(hiByte, i1, i2, l1, l2))
                     jjCheckNAddTwoStates(17, 18);
                  break;
               case 17:
                  if (jjCanMove_1(hiByte, i1, i2, l1, l2))
                     jjCheckNAddTwoStates(17, 18);
                  break;
               case 19:
                  if (!jjCanMove_1(hiByte, i1, i2, l1, l2))
                     break;
                  if (kind > 8)
                     kind = 8;
                  jjCheckNAddTwoStates(14, 19);
                  break;
               case 20:
                  if (jjCanMove_1(hiByte, i1, i2, l1, l2))
                     jjCheckNAddStates(30, 37);
                  break;
               case 21:
                  if (jjCanMove_1(hiByte, i1, i2, l1, l2))
                     jjCheckNAddTwoStates(21, 22);
                  break;
               case 23:
                  if (!jjCanMove_1(hiByte, i1, i2, l1, l2))
                     break;
                  if (kind > 1)
                     kind = 1;
                  jjstateSet[jjnewStateCnt++] = 23;
                  break;
               case 24:
                  if (jjCanMove_1(hiByte, i1, i2, l1, l2))
                     jjCheckNAddTwoStates(24, 25);
                  break;
               case 26:
                  if (!jjCanMove_1(hiByte, i1, i2, l1, l2))
                     break;
                  if (kind > 3)
                     kind = 3;
                  jjCheckNAddTwoStates(25, 26);
                  break;
               case 27:
                  if (jjCanMove_1(hiByte, i1, i2, l1, l2))
                     jjCheckNAddTwoStates(27, 28);
                  break;
               case 29:
                  if (jjCanMove_1(hiByte, i1, i2, l1, l2))
                     jjAddStates(44, 45);
                  break;
               case 31:
                  if (jjCanMove_1(hiByte, i1, i2, l1, l2))
                     jjCheckNAddTwoStates(31, 32);
                  break;
               case 33:
                  if (!jjCanMove_1(hiByte, i1, i2, l1, l2))
                     break;
                  if (kind > 5)
                     kind = 5;
                  jjstateSet[jjnewStateCnt++] = 33;
                  break;
               case 34:
                  if (!jjCanMove_1(hiByte, i1, i2, l1, l2))
                     break;
                  if (kind > 2)
                     kind = 2;
                  jjCheckNAddStates(0, 17);
                  break;
               case 35:
                  if (!jjCanMove_1(hiByte, i1, i2, l1, l2))
                     break;
                  if (kind > 2)
                     kind = 2;
                  jjCheckNAdd(35);
                  break;
               case 36:
                  if (jjCanMove_1(hiByte, i1, i2, l1, l2))
                     jjCheckNAddStates(24, 26);
                  break;
               case 38:
                  if (jjCanMove_1(hiByte, i1, i2, l1, l2))
                     jjCheckNAddStates(27, 29);
                  break;
               case 40:
                  if (jjCanMove_1(hiByte, i1, i2, l1, l2))
                     jjCheckNAddTwoStates(40, 41);
                  break;
               case 42:
                  if (!jjCanMove_1(hiByte, i1, i2, l1, l2))
                     break;
                  if (kind > 6)
                     kind = 6;
                  jjCheckNAddTwoStates(41, 42);
                  break;
               case 43:
                  if (jjCanMove_1(hiByte, i1, i2, l1, l2))
                     jjCheckNAddTwoStates(43, 44);
                  break;
               case 45:
                  if (!jjCanMove_1(hiByte, i1, i2, l1, l2))
                     break;
                  if (kind > 7)
                     kind = 7;
                  jjCheckNAddTwoStates(44, 45);
                  break;
               case 46:
                  if (jjCanMove_1(hiByte, i1, i2, l1, l2))
                     jjCheckNAddTwoStates(46, 47);
                  break;
               case 48:
                  if (jjCanMove_1(hiByte, i1, i2, l1, l2))
                     jjAddStates(54, 55);
                  break;
               case 49:
                  if (!jjCanMove_0(hiByte, i1, i2, l1, l2))
                     break;
                  if (kind > 8)
                     kind = 8;
                  jjCheckNAdd(50);
                  break;
               case 50:
                  if (!jjCanMove_1(hiByte, i1, i2, l1, l2))
                     break;
                  if (kind > 8)
                     kind = 8;
                  jjCheckNAdd(50);
                  break;
               case 51:
                  if (jjCanMove_1(hiByte, i1, i2, l1, l2))
                     jjCheckNAddTwoStates(51, 52);
                  break;
               case 52:
                  if (jjCanMove_0(hiByte, i1, i2, l1, l2))
                     jjCheckNAddTwoStates(1, 2);
                  break;
               case 53:
                  if (jjCanMove_1(hiByte, i1, i2, l1, l2))
                     jjCheckNAddTwoStates(53, 54);
                  break;
               case 55:
                  if (jjCanMove_1(hiByte, i1, i2, l1, l2))
                     jjAddStates(56, 57);
                  break;
               case 56:
                  if (jjCanMove_0(hiByte, i1, i2, l1, l2))
                     jjCheckNAddTwoStates(57, 58);
                  break;
               case 57:
                  if (jjCanMove_1(hiByte, i1, i2, l1, l2))
                     jjCheckNAddTwoStates(57, 58);
                  break;
               case 59:
                  if (!jjCanMove_1(hiByte, i1, i2, l1, l2))
                     break;
                  if (kind > 8)
                     kind = 8;
                  jjCheckNAddTwoStates(54, 59);
                  break;
               case 60:
                  if (jjCanMove_1(hiByte, i1, i2, l1, l2))
                     jjCheckNAddTwoStates(60, 61);
                  break;
               case 61:
                  if (jjCanMove_0(hiByte, i1, i2, l1, l2))
                     jjCheckNAddTwoStates(4, 5);
                  break;
               case 62:
                  if (jjCanMove_1(hiByte, i1, i2, l1, l2))
                     jjCheckNAddTwoStates(62, 63);
                  break;
               case 64:
                  if (jjCanMove_1(hiByte, i1, i2, l1, l2))
                     jjAddStates(58, 59);
                  break;
               case 65:
                  if (jjCanMove_0(hiByte, i1, i2, l1, l2))
                     jjCheckNAddTwoStates(66, 67);
                  break;
               case 66:
                  if (jjCanMove_1(hiByte, i1, i2, l1, l2))
                     jjCheckNAddTwoStates(66, 67);
                  break;
               case 68:
                  if (jjCanMove_1(hiByte, i1, i2, l1, l2))
                     jjAddStates(60, 61);
                  break;
               case 70:
                  if (jjCanMove_1(hiByte, i1, i2, l1, l2))
                     jjAddStates(62, 63);
                  break;
               case 71:
                  if (!jjCanMove_0(hiByte, i1, i2, l1, l2))
                     break;
                  if (kind > 8)
                     kind = 8;
                  jjCheckNAddTwoStates(67, 72);
                  break;
               case 72:
                  if (!jjCanMove_1(hiByte, i1, i2, l1, l2))
                     break;
                  if (kind > 8)
                     kind = 8;
                  jjCheckNAddTwoStates(67, 72);
                  break;
               case 73:
                  if (jjCanMove_1(hiByte, i1, i2, l1, l2))
                     jjCheckNAddTwoStates(73, 74);
                  break;
               case 74:
                  if (jjCanMove_0(hiByte, i1, i2, l1, l2))
                     jjCheckNAddTwoStates(11, 12);
                  break;
               default : break;
            }
         } while(i != startsAt);
      }
      if (kind != 0x7fffffff)
      {
         jjmatchedKind = kind;
         jjmatchedPos = curPos;
         kind = 0x7fffffff;
      }
      ++curPos;
      if ((i = jjnewStateCnt) == (startsAt = 75 - (jjnewStateCnt = startsAt)))
         return curPos;
      try { curChar = input_stream.readChar(); }
      catch(java.io.IOException e) { return curPos; }
   }
}
static final int[] jjnextStates = {
   35, 36, 37, 39, 43, 44, 46, 47, 51, 52, 53, 54, 60, 61, 62, 63, 
   73, 74, 1, 2, 4, 5, 11, 12, 36, 37, 39, 37, 38, 39, 21, 22, 
   24, 25, 27, 28, 31, 32, 1, 2, 11, 12, 17, 18, 29, 30, 57, 58, 
   6, 7, 8, 9, 15, 16, 48, 49, 55, 56, 64, 65, 68, 69, 70, 71, 
};
private static final boolean jjCanMove_0(int hiByte, int i1, int i2, long l1, long l2)
{
   switch(hiByte)
   {
      case 6:
         return ((jjbitVec2[i2] & l2) != 0L);
      case 11:
         return ((jjbitVec3[i2] & l2) != 0L);
      case 13:
         return ((jjbitVec4[i2] & l2) != 0L);
      case 14:
         return ((jjbitVec5[i2] & l2) != 0L);
      case 16:
         return ((jjbitVec6[i2] & l2) != 0L);
      default :
         if ((jjbitVec0[i1] & l1) != 0L)
            if ((jjbitVec1[i2] & l2) == 0L)
               return false;
            else
            return true;
         return false;
   }
}
private static final boolean jjCanMove_1(int hiByte, int i1, int i2, long l1, long l2)
{
   switch(hiByte)
   {
      case 0:
         return ((jjbitVec9[i2] & l2) != 0L);
      default :
         if ((jjbitVec7[i1] & l1) != 0L)
            return true;
         return false;
   }
}

/** Token literal values. */
public static final String[] jjstrLiteralImages = {
"", null, null, null, null, null, null, null, null, null, null, null, null, 
null, null, };

/** Lexer state names. */
public static final String[] lexStateNames = {
   "DEFAULT",
};
static final long[] jjtoToken = {
   0x1ffL, 
};
static final long[] jjtoSkip = {
   0x4000L, 
};
protected SimpleCharStream input_stream;
private final int[] jjrounds = new int[75];
private final int[] jjstateSet = new int[150];
protected char curChar;
/** Constructor. */
public WordTokenizerTokenManager(SimpleCharStream stream){
   if (SimpleCharStream.staticFlag)
      throw new Error("ERROR: Cannot use a static CharStream class with a non-static lexical analyzer.");
   input_stream = stream;
}

/** Constructor. */
public WordTokenizerTokenManager(SimpleCharStream stream, int lexState){
   this(stream);
   SwitchTo(lexState);
}

/** Reinitialise parser. */
public void ReInit(SimpleCharStream stream)
{
   jjmatchedPos = jjnewStateCnt = 0;
   curLexState = defaultLexState;
   input_stream = stream;
   ReInitRounds();
}
private void ReInitRounds()
{
   int i;
   jjround = 0x80000001;
   for (i = 75; i-- > 0;)
      jjrounds[i] = 0x80000000;
}

/** Reinitialise parser. */
public void ReInit(SimpleCharStream stream, int lexState)
{
   ReInit(stream);
   SwitchTo(lexState);
}

/** Switch to specified lex state. */
public void SwitchTo(int lexState)
{
   if (lexState >= 1 || lexState < 0)
      throw new TokenMgrError("Error: Ignoring invalid lexical state : " + lexState + ". State unchanged.", TokenMgrError.INVALID_LEXICAL_STATE);
   else
      curLexState = lexState;
}

protected Token jjFillToken()
{
   final Token t;
   final String curTokenImage;
   final int beginLine;
   final int endLine;
   final int beginColumn;
   final int endColumn;
   String im = jjstrLiteralImages[jjmatchedKind];
   curTokenImage = (im == null) ? input_stream.GetImage() : im;
   beginLine = input_stream.getBeginLine();
   beginColumn = input_stream.getBeginColumn();
   endLine = input_stream.getEndLine();
   endColumn = input_stream.getEndColumn();
   t = Token.newToken(jjmatchedKind, curTokenImage);

   t.beginLine = beginLine;
   t.endLine = endLine;
   t.beginColumn = beginColumn;
   t.endColumn = endColumn;

   return t;
}

int curLexState = 0;
int defaultLexState = 0;
int jjnewStateCnt;
int jjround;
int jjmatchedPos;
int jjmatchedKind;

/** Get the next Token. */
public Token getNextToken() 
{
  Token matchedToken;
  int curPos = 0;

  EOFLoop :
  for (;;)
  {
   try
   {
      curChar = input_stream.BeginToken();
   }
   catch(java.io.IOException e)
   {
      jjmatchedKind = 0;
      matchedToken = jjFillToken();
      return matchedToken;
   }

   jjmatchedKind = 0x7fffffff;
   jjmatchedPos = 0;
   curPos = jjMoveStringLiteralDfa0_0();
   if (jjmatchedPos == 0 && jjmatchedKind > 14)
   {
      jjmatchedKind = 14;
   }
   if (jjmatchedKind != 0x7fffffff)
   {
      if (jjmatchedPos + 1 < curPos)
         input_stream.backup(curPos - jjmatchedPos - 1);
      if ((jjtoToken[jjmatchedKind >> 6] & (1L << (jjmatchedKind & 077))) != 0L)
      {
         matchedToken = jjFillToken();
         return matchedToken;
      }
      else
      {
         continue EOFLoop;
      }
   }
   int error_line = input_stream.getEndLine();
   int error_column = input_stream.getEndColumn();
   String error_after = null;
   boolean EOFSeen = false;
   try { input_stream.readChar(); input_stream.backup(1); }
   catch (java.io.IOException e1) {
      EOFSeen = true;
      error_after = curPos <= 1 ? "" : input_stream.GetImage();
      if (curChar == '\n' || curChar == '\r') {
         error_line++;
         error_column = 0;
      }
      else
         error_column++;
   }
   if (!EOFSeen) {
      input_stream.backup(1);
      error_after = curPos <= 1 ? "" : input_stream.GetImage();
   }
   throw new TokenMgrError(EOFSeen, curLexState, error_line, error_column, error_after, curChar, TokenMgrError.LEXICAL_ERROR);
  }
}

private void jjCheckNAdd(int state)
{
   if (jjrounds[state] != jjround)
   {
      jjstateSet[jjnewStateCnt++] = state;
      jjrounds[state] = jjround;
   }
}
private void jjAddStates(int start, int end)
{
   do {
      jjstateSet[jjnewStateCnt++] = jjnextStates[start];
   } while (start++ != end);
}
private void jjCheckNAddTwoStates(int state1, int state2)
{
   jjCheckNAdd(state1);
   jjCheckNAdd(state2);
}

private void jjCheckNAddStates(int start, int end)
{
   do {
      jjCheckNAdd(jjnextStates[start]);
   } while (start++ != end);
}

}
