// EnglishGrammaticalStructureTest -- unit tests for Stanford dependencies.
// Copyright (c) 2005, 2011, 2013 The Board of Trustees of
// The Leland Stanford Junior University. All Rights Reserved.
//
// This program is free software; you can redistribute it and/or
// modify it under the terms of the GNU General Public License
// as published by the Free Software Foundation; either version 2
// of the License, or (at your option) any later version.
//
// This program is distributed in the hope that it will be useful,
// but WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
// GNU General Public License for more details.
//
// You should have received a copy of the GNU General Public License
// along with this program; if not, write to the Free Software
// Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
//
// For more information, bug reports, fixes, contact:
//    Christopher Manning
//    Dept of Computer Science, Gates 1A
//    Stanford CA 94305-9010
//    USA
//    Support/Questions: parser-user@lists.stanford.edu
//    Licensing: parser-support@lists.stanford.edu

package edu.stanford.nlp.trees.international.pennchinese;

import java.util.*;
import junit.framework.TestCase;
import edu.stanford.nlp.trees.*;
import edu.stanford.nlp.util.Pair;

/**
 * Test cases for conversion of Chinese TreeBank to Universal Stanford
 * Dependencies with Chinese characteristics.
 * This code is adapted from EnglishGrammaticalStructureTest.java
 *
 * @author Percy Liang
 */
public class ChineseGrammaticalStructureTest extends TestCase {
  // Return a string which is the concatenation of |items|, with a new line after each line.
  // "a", "b" => "a\nb\n"
  private String C(String... items) {
    StringBuilder out = new StringBuilder();
    for (String x : items) {
      out.append(x);
      out.append('\n');
    }
    return out.toString();
  }

  // Create a new example
  Pair<String, String> T(String tree, String ans) {
    return new Pair<String, String>(tree, ans);
  }

  List<Pair<String, String>> Examples(Pair<String, String>... examples) {
    return Arrays.asList(examples);
  }

  /**
   * Tests that we can extract the basic grammatical relations correctly from
   * some hard-coded trees.
   *
   * Sentence examples from the manual to at least test each relation.
   *
   */
  public void testBasicRelations() {
    Pair<String, String>[] examples = new Pair[] {
      // Gloss: Shanghai Pudong de orderly advance
      T("(NP (DNP (NP (NP (NR 浦东)) (NP (NN 开发))) (DEG 的)) (ADJP (JJ 有序)) (NP (NN 进行)))",
        C("nn(开发-2, 浦东-1)", "assmod(进行-5, 开发-2)", "case(开发-2, 的-3)", "amod(进行-5, 有序-4)", "root(ROOT-0, 进行-5)")),

      // Gloss: Shanghai Pudong expand and legal-system synchronous
      T("(ROOT (IP (NP (NP (NR 上海) (NR 浦东)) (NP (NN 开发) (CC 与) (NN 法制) (NN 建设))) (VP (VV 同步))))",
        C("nn(浦东-2, 上海-1)", "nn(建设-6, 浦东-2)", "conj(建设-6, 开发-3)", "cc(建设-6, 与-4)", "nn(建设-6, 法制-5)", "nsubj(同步-7, 建设-6)", "root(ROOT-0, 同步-7)")),

      // Gloss: this-year
      T("(LCP (NP (NT 近年)) (LC 来))",
        C("root(ROOT-0, 近年-1)", "case(近年-1, 来-2)")),

      // Gloss: according country and Shanghai de relevant law
      T("(PP (P 根据) (NP (DNP (NP (NP (NN 国家)) (CC 和) (NP (NR 上海市))) (DEG 的)) (ADJP (JJ 有关)) (NP (NN 规定))))",
        C("case(规定-7, 根据-1)", "conj(上海市-4, 国家-2)", "cc(上海市-4, 和-3)", "assmod(规定-7, 上海市-4)", "case(上海市-4, 的-5)", "amod(规定-7, 有关-6)", "root(ROOT-0, 规定-7)")),

      // Gloss: building is expand Shanghai de primary economic activity
      T("(IP (NP (NN 建筑)) (VP (VC 是) (NP (CP (IP (VP (VV 开发) (NP (NR 浦东)))) (DEC 的)) (QP (CD 一) (CLP (M 项))) (ADJP (JJ 主要)) (NP (NN 经济) (NN 活动)))))",
        C("nsubj(活动-10, 建筑-1)", "cop(活动-10, 是-2)", "relcl(活动-10, 开发-3)", "dobj(开发-3, 浦东-4)", "mark(开发-3, 的-5)", "nummod(项-7, 一-6)", "clf(活动-10, 项-7)", "amod(活动-10, 主要-8)", "nn(活动-10, 经济-9)", "root(ROOT-0, 活动-10)")),

      // Gloss: nickel has-been named modern industry de vitamins
      T("(IP (NP (NN 镍)) (VP (SB 被) (VP (VV 称作) (NP (PU “) (DNP (NP (ADJP (JJ 现代)) (NP (NN 工业))) (DEG 的)) (NP (NN 维生素)) (PU ”)))))",
        C("nsubjpass(称作-3, 镍-1)", "auxpass(称作-3, 被-2)", "root(ROOT-0, 称作-3)", "amod(工业-6, 现代-5)", "assmod(维生素-8, 工业-6)", "case(工业-6, 的-7)", "dobj(称作-3, 维生素-8)")),

      // Gloss: once revealed then was included legal-system path
      T("(IP (VP (VP (ADVP (AD 一)) (VP (VV 出现))) (VP (ADVP (AD 就)) (VP (SB 被) (VP (VV 纳入) (NP (NN 法制) (NN 轨道)))))))))))",
        C("advmod(出现-2, 一-1)", "root(ROOT-0, 出现-2)", "advmod(纳入-5, 就-3)", "auxpass(纳入-5, 被-4)", "dep(出现-2, 纳入-5)", "nn(轨道-7, 法制-6)", "dobj(纳入-5, 轨道-7)")),

      // TODO(pliang): add more test cases for all the relations not covered (see WARNING below)
    };

    // Make sure all the relations are tested for
    Set<String> testedRelations = new HashSet<String>();
    for (Pair<String, String> ex : examples) {
      for (String item : ex.second.split("\n"))
        testedRelations.add(item.substring(0, item.indexOf('(')));
    }
    for (String relation : ChineseGrammaticalRelations.shortNameToGRel.keySet()) {
      // TODO(pliang): don't warn for abstract relations like 'subj'
      if (!testedRelations.contains(relation))
        System.out.println("WARNING: relation '" + relation + "' not tested");
    }

    TreeReaderFactory trf = new PennTreeReaderFactory();
    for (Pair<String, String> ex : examples) {
      String testTree = ex.first;
      String testAnswer = ex.second;

      // specifying our own TreeReaderFactory is vital so that functional
      // categories - that is -TMP and -ADV in particular - are not stripped off
      Tree tree = Tree.valueOf(testTree, trf);
      GrammaticalStructure gs = new ChineseGrammaticalStructure(tree);

      assertEquals("Unexpected CC processed dependencies for tree "+testTree,
          testAnswer,
          ChineseGrammaticalStructure.dependenciesToString(gs, gs.typedDependenciesCCprocessed(true), tree, false, false));
    }
  }

  public static void main(String[] args) {
    new ChineseGrammaticalStructureTest().testBasicRelations();
  }
}
