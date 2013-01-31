package edu.colorado.clear.tutorial.tweetpos;

import java.io.File;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.cas.CAS;
import org.apache.uima.collection.CollectionReader;
import org.apache.uima.jcas.JCas;
import org.cleartk.classifier.jar.DefaultSequenceDataWriterFactory;
import org.cleartk.classifier.jar.GenericJarClassifierFactory;
import org.cleartk.classifier.jar.Train;
import org.cleartk.classifier.mallet.MalletCRFStringOutcomeDataWriter;
import org.cleartk.eval.AnnotationStatistics;
import org.cleartk.eval.Evaluation_ImplBase;
import org.cleartk.token.type.Token;
import org.cleartk.util.ae.UriToDocumentTextAnnotator;
import org.cleartk.util.cr.UriCollectionReader;
import org.uimafit.component.JCasAnnotator_ImplBase;
import org.uimafit.component.ViewCreatorAnnotator;
import org.uimafit.factory.AggregateBuilder;
import org.uimafit.factory.AnalysisEngineFactory;
import org.uimafit.pipeline.JCasIterable;
import org.uimafit.pipeline.SimplePipeline;
import org.uimafit.util.JCasUtil;

import com.google.common.base.Function;

public class TweetPosEval extends Evaluation_ImplBase<File, AnnotationStatistics<String>> {
	
	public static void main(String[] args) throws Exception {
		File outputDirectory = new File("target/models");
		List<File> trainItems = Arrays.asList(new File("src/main/resources/twpos-data-v0.2.1/train"));
		List<File> testItems = Arrays.asList(new File("src/main/resources/twpos-data-v0.2.1/dev"));
		TweetPosEval eval = new TweetPosEval(outputDirectory);
		AnnotationStatistics<String> stats = eval.trainAndTest(trainItems, testItems);
		System.out.println(stats);
	}

	public TweetPosEval(File baseDirectory) {
		super(baseDirectory);
	}

	@Override
	protected CollectionReader getCollectionReader(List<File> items)
			throws Exception {
		return UriCollectionReader.getCollectionReaderFromFiles(items);
	}

	@Override
	protected void train(CollectionReader collectionReader, File directory)
			throws Exception {
		AggregateBuilder builder = new AggregateBuilder();
		builder.add(AnalysisEngineFactory.createPrimitiveDescription(
				ViewCreatorAnnotator.class,
				ViewCreatorAnnotator.PARAM_VIEW_NAME,
				TweetPosReaderAnnotator.TWEET_POS_CONLL_VIEW));
		builder.add(UriToDocumentTextAnnotator.getDescription(),
				CAS.NAME_DEFAULT_SOFA,
				TweetPosReaderAnnotator.TWEET_POS_CONLL_VIEW);
		builder.add(AnalysisEngineFactory.createPrimitiveDescription(
				TweetPosReaderAnnotator.class,
				TweetPosReaderAnnotator.PARAM_INPUT_VIEW,
				TweetPosReaderAnnotator.TWEET_POS_CONLL_VIEW));
		builder.add(AnalysisEngineFactory.createPrimitiveDescription(
				TweetPosTagger.class,
				TweetPosTagger.PARAM_IS_TRAINING,
				true,
				DefaultSequenceDataWriterFactory.PARAM_OUTPUT_DIRECTORY,
				directory,
				DefaultSequenceDataWriterFactory.PARAM_DATA_WRITER_CLASS_NAME,
				MalletCRFStringOutcomeDataWriter.class));
		SimplePipeline.runPipeline(collectionReader, builder.createAggregate());
		Train.main(directory, "--iterations", "100");
	}

	@Override
	protected AnnotationStatistics<String> test(CollectionReader collectionReader, File directory)
			throws Exception {
		String goldViewName = "GoldView";
		AggregateBuilder builder = new AggregateBuilder();
		builder.add(AnalysisEngineFactory.createPrimitiveDescription(
				ViewCreatorAnnotator.class,
				ViewCreatorAnnotator.PARAM_VIEW_NAME,
				goldViewName));
		builder.add(AnalysisEngineFactory.createPrimitiveDescription(
				ViewCreatorAnnotator.class,
				ViewCreatorAnnotator.PARAM_VIEW_NAME,
				TweetPosReaderAnnotator.TWEET_POS_CONLL_VIEW));
		builder.add(UriToDocumentTextAnnotator.getDescription(),
				CAS.NAME_DEFAULT_SOFA,
				TweetPosReaderAnnotator.TWEET_POS_CONLL_VIEW);
		builder.add(AnalysisEngineFactory.createPrimitiveDescription(
				TweetPosReaderAnnotator.class,
				TweetPosReaderAnnotator.PARAM_INPUT_VIEW,
				TweetPosReaderAnnotator.TWEET_POS_CONLL_VIEW),
				CAS.NAME_DEFAULT_SOFA,
				goldViewName);
		builder.add(AnalysisEngineFactory.createPrimitiveDescription(
				TweetPosReaderAnnotator.class,
				TweetPosReaderAnnotator.PARAM_INPUT_VIEW,
				TweetPosReaderAnnotator.TWEET_POS_CONLL_VIEW));
		builder.add(AnalysisEngineFactory.createPrimitiveDescription(
				DeletePos.class));
		builder.add(AnalysisEngineFactory.createPrimitiveDescription(
				TweetPosTagger.class,
				TweetPosTagger.PARAM_IS_TRAINING,
				false,
				GenericJarClassifierFactory.PARAM_CLASSIFIER_JAR_PATH,
				new File(directory, "model.jar")));
		
		AnnotationStatistics<String> stats = new AnnotationStatistics<String>();
		Function<Token, ?> annotationToSpan = AnnotationStatistics.annotationToSpan();
		Function<Token, String> annotationToOutcome = AnnotationStatistics.annotationToFeatureValue("pos");
		for (JCas jCas : new JCasIterable(collectionReader, builder.createAggregate())) {
			JCas goldView = jCas.getView(goldViewName);
			JCas systemView = jCas.getView(CAS.NAME_DEFAULT_SOFA);
			Collection<Token> goldTokens = JCasUtil.select(goldView, Token.class);
			Collection<Token> systemTokens = JCasUtil.select(systemView, Token.class);
			stats.add(goldTokens, systemTokens, annotationToSpan, annotationToOutcome);
		}
		return stats;
	}

	public static class DeletePos extends JCasAnnotator_ImplBase {

		@Override
		public void process(JCas jCas) throws AnalysisEngineProcessException {
			for (Token token : JCasUtil.select(jCas, Token.class)) {
				token.setPos(null);
			}
		}
		
	}
}
