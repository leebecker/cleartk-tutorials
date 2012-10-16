package edu.colorado.clear.tweetpos;

import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.jcas.JCas;
import org.cleartk.token.type.Sentence;
import org.cleartk.token.type.Token;
import org.uimafit.component.JCasAnnotator_ImplBase;
import org.uimafit.util.JCasUtil;


public class DumpTweetPos extends JCasAnnotator_ImplBase {

	@Override
	public void process(JCas jCas) throws AnalysisEngineProcessException {
		for (Sentence sentence : JCasUtil.select(jCas, Sentence.class)) {
			System.out.println(sentence.getCoveredText());
			for (Token token : JCasUtil.selectCovered(jCas, Token.class, sentence)) {
				System.out.printf("%s/%s ", token.getCoveredText(), token.getPos());
			}
			System.out.println();
			System.out.println();
		}
	}


}
