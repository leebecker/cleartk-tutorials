package edu.colorado.clear.tutorial.tweetpos;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.jcas.JCas;
import org.cleartk.classifier.CleartkSequenceAnnotator;
import org.cleartk.classifier.Feature;
import org.cleartk.classifier.Instances;
import org.cleartk.classifier.feature.extractor.CleartkExtractor;
import org.cleartk.classifier.feature.extractor.simple.CoveredTextExtractor;
import org.cleartk.token.type.Sentence;
import org.cleartk.token.type.Token;
import org.uimafit.util.JCasUtil;

public class TweetPosTagger extends CleartkSequenceAnnotator<String> {
	
	CleartkExtractor extractor = new CleartkExtractor(
			Token.class,
			new CoveredTextExtractor(),
			new CleartkExtractor.Preceding(3),
			new CleartkExtractor.Following(3));

	@Override
	public void process(JCas jCas) throws AnalysisEngineProcessException {
		for (Sentence sentence : JCasUtil.select(jCas, Sentence.class)) {
			List<List<Feature>> featureLists = new ArrayList<List<Feature>>(); 
			List<Token> tokens = JCasUtil.selectCovered(jCas, Token.class, sentence);
			for (Token token : tokens) {
				List<Feature> features = new ArrayList<Feature>();
				features.add(new Feature("text", token.getCoveredText()));
				features.add(new Feature("length", token.getCoveredText().length()));
				features.addAll(this.extractor.extractWithin(jCas, token, sentence));
				featureLists.add(features);
			}
			if (this.isTraining()) {
				List<String> outcomes = new ArrayList<String>();
				for (Token token : tokens) {
					outcomes.add(token.getPos());
				}
				this.dataWriter.write(Instances.toInstances(outcomes, featureLists));
			} else {
				List<String> outcomes = this.classifier.classify(featureLists);
				Iterator<String> outcomeIter = outcomes.iterator();
				for (Token token : tokens) {
					token.setPos(outcomeIter.next());
				}
			}
		}
	}

}
