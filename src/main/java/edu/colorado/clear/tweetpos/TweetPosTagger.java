package edu.colorado.clear.tweetpos;

import java.util.ArrayList;
import java.util.List;

import org.apache.uima.UimaContext;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.jcas.JCas;
import org.apache.uima.resource.ResourceInitializationException;
import org.cleartk.classifier.CleartkAnnotator;
import org.cleartk.classifier.Instance;
import org.cleartk.classifier.feature.extractor.CleartkExtractor;
import org.cleartk.classifier.feature.extractor.CleartkExtractor.Following;
import org.cleartk.classifier.feature.extractor.CleartkExtractor.Preceding;
import org.cleartk.classifier.feature.extractor.simple.CoveredTextExtractor;
import org.cleartk.classifier.feature.extractor.simple.SimpleFeatureExtractor;
import org.cleartk.classifier.feature.function.CapitalTypeFeatureFunction;
import org.cleartk.classifier.feature.function.CharacterNGramFeatureFunction;
import org.cleartk.classifier.feature.function.FeatureFunctionExtractor;
import org.cleartk.classifier.feature.function.LowerCaseFeatureFunction;
import org.cleartk.classifier.feature.function.NumericTypeFeatureFunction;
import org.cleartk.token.type.Sentence;
import org.cleartk.token.type.Token;
import org.uimafit.util.JCasUtil;

public class TweetPosTagger extends CleartkAnnotator<String>{
	
	
	private SimpleFeatureExtractor tokenFeatureExtractor;

	private List<CleartkExtractor> contextFeatureExtractors;
	
	@Override
	public void initialize(UimaContext context)
			throws ResourceInitializationException {
		super.initialize(context);
		
	    // alias for NGram feature parameters
	    CharacterNGramFeatureFunction.Orientation fromRight = CharacterNGramFeatureFunction.Orientation.RIGHT_TO_LEFT;
		
		// a feature extractor that creates features corresponding to the word, the word lower cased
	    // the capitalization of the word, the numeric characterization of the word, and character ngram
	    // suffixes of length 2 and 3.
	    this.tokenFeatureExtractor = new FeatureFunctionExtractor(
	        new CoveredTextExtractor(),
	        new LowerCaseFeatureFunction(),
	        new CapitalTypeFeatureFunction(),
	        new NumericTypeFeatureFunction(),
	        new CharacterNGramFeatureFunction(fromRight, 0, 2),
	        new CharacterNGramFeatureFunction(fromRight, 0, 3));
		
	    contextFeatureExtractors = new ArrayList<CleartkExtractor>();
		this.contextFeatureExtractors.add(new CleartkExtractor(
				Token.class,
				new CoveredTextExtractor(),
				new Preceding(2),
				new Following(2)));
		
	}

	@Override
	public void process(JCas jCas) throws AnalysisEngineProcessException {
		
		for (Sentence sentence : JCasUtil.select(jCas, Sentence.class)) {
			for (Token token : JCasUtil.selectCovered(jCas, Token.class, sentence)) {
				
				Instance<String> instance = new Instance<String>();
				
				// Extract features
				instance.addAll(this.tokenFeatureExtractor.extract(jCas, token));
				for (CleartkExtractor extractor : this.contextFeatureExtractors) {
					instance.addAll(extractor.extractWithin(jCas, token, sentence));
				}
				
				if (this.isTraining()) {
					// Write out to data file for training
					instance.setOutcome(token.getPos());
					this.dataWriter.write(instance);
				} else {
					// Classify and write POS-tag back to token in CAS
					String tag = this.classifier.classify(instance.getFeatures());
					token.setPos(tag);
				}
			}
		}
	}
}
