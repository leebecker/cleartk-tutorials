package edu.colorado.clear.tutorial.tweetpos;

import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.cas.CASException;
import org.apache.uima.jcas.JCas;
import org.cleartk.token.type.Sentence;
import org.cleartk.token.type.Token;
import org.uimafit.component.JCasAnnotator_ImplBase;
import org.uimafit.descriptor.ConfigurationParameter;
import org.uimafit.factory.ConfigurationParameterFactory;

import com.google.common.base.Splitter;

public class TweetPosReaderAnnotator extends JCasAnnotator_ImplBase{
	
	@ConfigurationParameter(
			mandatory = false,
			description = "Specifies whether or not to populate tokens with POS tags.  Default = true")
	protected Boolean readPosTags = true;


	public static final String PARAM_READ_POS_TAGS = ConfigurationParameterFactory.createConfigurationParameterName(
			TweetPosReaderAnnotator.class,
			"readPosTags");
	
	
	@ConfigurationParameter(
			mandatory = false,
			description = "View containing twpos CONLL formatted data",
			defaultValue = TWEET_POS_CONLL_VIEW)
	protected String inputView;
	
	public static final String PARAM_INPUT_VIEW = ConfigurationParameterFactory.createConfigurationParameterName(
			TweetPosReaderAnnotator.class,
			"inputView");
	
	public static final String TWEET_POS_CONLL_VIEW = "tweet_pos_conll_view";
	
	
	@Override
	public void process(JCas jCas) throws AnalysisEngineProcessException {
		
		JCas conllView;
		try {
			conllView = jCas.getView(this.inputView);
		} catch (CASException e) {
			throw new AnalysisEngineProcessException(e);
		}
		
		StringBuilder builder = new StringBuilder();
		
		int sentenceBegin = builder.length();
		int sentenceEnd;
		
		for (String line : Splitter.on("\n").split(conllView.getDocumentText())) {
			if (line.matches("^$")) {
				// Empty line indicates the end of a sentence
				sentenceEnd = builder.length();
				if (sentenceEnd > sentenceBegin) {
					Sentence sentence = new Sentence(jCas, sentenceBegin, sentenceEnd);
					sentence.addToIndexes();
					builder.append("\n");
					sentenceBegin = builder.length();
				}
			} else {
				// Normal lines are token and POS tag
				String[] parts = line.split("\\t");
				String tokenText = parts[0];
				String tokenPos = parts[1];
				
				int tokenBegin = builder.length();
				int tokenEnd = tokenBegin + tokenText.length();
				Token token = new Token(jCas, tokenBegin, tokenEnd);
				if (this.readPosTags) {
					token.setPos(tokenPos);
				}
				token.addToIndexes();
				builder.append(tokenText);
				builder.append(" ");
			}
		}
		
		jCas.setDocumentText(builder.toString());
	}
}
