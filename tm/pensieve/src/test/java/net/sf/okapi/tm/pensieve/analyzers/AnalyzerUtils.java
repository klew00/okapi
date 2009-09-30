package net.sf.okapi.tm.pensieve.analyzers;

import junit.framework.Assert;
import org.apache.lucene.util.AttributeSource;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.SimpleAnalyzer;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.Token;
import org.apache.lucene.analysis.tokenattributes.TermAttribute;
import org.apache.lucene.analysis.tokenattributes.TypeAttribute;
import org.apache.lucene.analysis.tokenattributes.PositionIncrementAttribute;
import org.apache.lucene.analysis.tokenattributes.OffsetAttribute;
import org.apache.lucene.analysis.standard.StandardAnalyzer;

import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import net.sf.okapi.tm.pensieve.Helper;
import org.apache.lucene.util.AttributeImpl;
import org.apache.lucene.util.AttributeSource.State;

public class AnalyzerUtils {
//  public static State[] tokensFromAnalysis(Analyzer analyzer,
//                                                     String text) throws IOException {
//    TokenStream stream =                                              //1
//           analyzer.tokenStream("contents", new StringReader(text));  //1
//
//    return tokensFromAnalysis(stream);
//  }
//
//  public static State[] tokensFromAnalysis(TokenStream stream)
//    throws IOException {
//    ArrayList tokenList = new ArrayList();
//    while (true) {
//      if (!stream.incrementToken())
//        break;
//
//      tokenList.add(stream.captureState());
//    }
//
//    return (State[]) tokenList.toArray(new State[0]);
//  }
//
//  public static String getTermStringFromState(State state) throws Exception {
//       AttributeSource token = new AttributeSource();
//      AttributeImpl ai = (AttributeImpl) Helper.getPrivateMember(state, "attribute");
//      token.addAttributeImpl(ai);
//      token.restoreState(state);
//      TermAttribute term = (TermAttribute) token.addAttribute(TermAttribute.class);
//      return term.term();
//    }
//
//  public static void displayTokens(Analyzer analyzer,
//                                   String text) throws Exception {
//    displayTokens(analyzer.tokenStream("contents", new StringReader(text)));
//  }
//
//  public static void displayTokens(TokenStream stream)
//    throws Exception {
//    State[] tokens = tokensFromAnalysis(stream);
//
//    for (int i = 0; i < tokens.length; i++) {
//      AttributeSource token = new AttributeSource();
//      AttributeImpl ai = (AttributeImpl) Helper.getPrivateMember(tokens[i], "attribute");
//      token.addAttributeImpl(ai);
//      token.restoreState(tokens[i]);
//      TermAttribute term = (TermAttribute) token.addAttribute(TermAttribute.class);
//      System.out.print("[" + term.term() + "] ");    //2
//    }
//  }
//
//  public static int getPositionIncrement(AttributeSource source) {
//    PositionIncrementAttribute attr = (PositionIncrementAttribute) source.addAttribute(PositionIncrementAttribute.class);
//    return attr.getPositionIncrement();
//  }
//
//  public static String getTerm(AttributeSource source) {
//    TermAttribute attr = (TermAttribute) source.addAttribute(TermAttribute.class);
//    return attr.term();
//  }
//
//  public static String getType(AttributeSource source) {
//    TypeAttribute attr = (TypeAttribute) source.addAttribute(TypeAttribute.class);
//    return attr.type();
//  }
//
//  public static void setPositionIncrement(AttributeSource source, int posIncr) {
//    PositionIncrementAttribute attr = (PositionIncrementAttribute) source.addAttribute(PositionIncrementAttribute.class);
//    attr.setPositionIncrement(posIncr);
//  }
//
//  public static void setTerm(AttributeSource source, String term) {
//    TermAttribute attr = (TermAttribute) source.addAttribute(TermAttribute.class);
//    attr.setTermBuffer(term);
//  }
//
//  public static void setType(AttributeSource source, String type) {
//    TypeAttribute attr = (TypeAttribute) source.addAttribute(TypeAttribute.class);
//    attr.setType(type);
//  }
//
//  public static void displayTokensWithPositions
//    (Analyzer analyzer, String text) throws IOException {
//    State[] tokens = tokensFromAnalysis(analyzer, text);
//
//    int position = 0;
//
//    for (int i = 0; i < tokens.length; i++) {
//      AttributeSource token = new AttributeSource();
//      token.restoreState(tokens[i]);
//
//      TermAttribute term = (TermAttribute) token.addAttribute(TermAttribute.class);
//
//      PositionIncrementAttribute posIncr =
//        (PositionIncrementAttribute)
//        token.addAttribute(PositionIncrementAttribute.class);
//
//      int increment = posIncr.getPositionIncrement();
//
//      if (increment > 0) {
//        position = position + increment;
//        System.out.println();
//        System.out.print(position + ": ");
//      }
//
//      System.out.print("[" + term.term() + "] ");
//    }
//    System.out.println();
//  }
//
//  public static void displayTokensWithFullDetails(Analyzer analyzer,
//                                                  String text) throws IOException {
//    State[] tokens = tokensFromAnalysis(analyzer, text);
//
//    int position = 0;
//
//    for (int i = 0; i < tokens.length; i++) {
//      AttributeSource token = new AttributeSource();
//              token.restoreState(tokens[i]);
//
//      TermAttribute term = (TermAttribute) token.addAttribute(TermAttribute.class);
//
//      PositionIncrementAttribute posIncr =
//        (PositionIncrementAttribute)
//        token.addAttribute(PositionIncrementAttribute.class);
//
//      OffsetAttribute offset = (OffsetAttribute) token.addAttribute(OffsetAttribute.class);
//
//      TypeAttribute type = (TypeAttribute) token.addAttribute(TypeAttribute.class);
//
//      int increment = posIncr.getPositionIncrement();
//
//      if (increment > 0) {
//        position = position + increment;
//        System.out.println();
//        System.out.print(position + ": ");
//      }
//
//      System.out.print("[" +
//                       term.term() + ":" +
//                       offset.startOffset() + "->" +
//                       offset.endOffset() + ":" +
//                       type.type() + "] ");
//    }
//    System.out.println();
//  }
//
//  public static void assertAnalyzesTo(Analyzer analyzer, String input,
//                                      String[] output) throws Exception {
//    TokenStream stream =
//        analyzer.tokenStream("field", new StringReader(input));
//
//    if (stream.getOnlyUseNewAPI()) {
//      TermAttribute termAttr = (TermAttribute) stream.addAttribute(TermAttribute.class);
//      for (int i=0; i<output.length; i++) {
//        Assert.assertTrue(stream.incrementToken());
//        Assert.assertEquals(output[i], termAttr.term());
//      }
//      Assert.assertFalse(stream.incrementToken());
//    } else {
//      Token reusableToken = new Token();
//      for (int i=0; i<output.length; i++) {
//        Token t = stream.next(reusableToken);
//        Assert.assertTrue(t != null);
//        Assert.assertEquals(output[i], t.term());
//      }
//      Assert.assertTrue(stream.next(reusableToken) == null);
//    }
//    stream.close();
//  }
//
//  public static void assertTokensEqual(
//                 AttributeSource[] tokens, String[] strings) {
//    Assert.assertEquals(strings.length, tokens.length);
//
//    for (int i = 0; i < tokens.length; i++) {
//      AttributeSource token = tokens[i];
//      TermAttribute term = (TermAttribute) token.addAttribute(TermAttribute.class);
//
//      Assert.assertEquals("index " + i,
//                          strings[i], term.term());
//    }
//  }
//
//  public static void displayPositionIncrements(Analyzer analyzer, String text)
//    throws IOException {
//    TokenStream stream = analyzer.tokenStream("contents", new StringReader(text));
//    PositionIncrementAttribute posIncr = (PositionIncrementAttribute)
//      stream.addAttribute(PositionIncrementAttribute.class);
//    while (stream.incrementToken()) {
//      System.out.println("posIncr=" + posIncr.getPositionIncrement());
//    }
//  }
//
//  public static void main(String[] args) throws IOException {
//    System.out.println("SimpleAnalyzer");
//    displayTokensWithFullDetails(new SimpleAnalyzer(),
//        "The quick brown fox....");
//
//    System.out.println("\n----");
//    System.out.println("StandardAnalyzer");
//    displayTokensWithFullDetails(new StandardAnalyzer(),
//        "I'll e-mail you at xyz@example.com");
//  }
}

/*
#1 Invoke analysis process
#2 Output token text surrounded by brackets
*/

