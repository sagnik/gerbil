package org.aksw.gerbil.dataset.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import org.aksw.gerbil.config.GerbilConfiguration;
import org.aksw.gerbil.semantic.kb.SimpleWhiteListBasedUriKBClassifier;
import org.aksw.gerbil.semantic.kb.UriKBClassifier;
import org.aksw.gerbil.test.EntityCheckerManagerSingleton4Tests;
import org.aksw.gerbil.test.SameAsRetrieverSingleton4Tests;
import org.aksw.gerbil.transfer.nif.Meaning;
import org.aksw.gerbil.transfer.nif.data.Annotation;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

@RunWith(Parameterized.class)
public class FilteringWhileLoadingTest {

    /**
     * @see org.aksw.gerbil.evaluate.EvaluatorFactory
     */
    private static UriKBClassifier loadInKBClassifier() {
        String kbs[] = GerbilConfiguration.getInstance().getStringArray("org.aksw.gerbil.evaluate.DefaultWellKnownKB");
        return new SimpleWhiteListBasedUriKBClassifier(kbs);
    }

    private static final UriKBClassifier GLOBAL_CLASSIFIER = loadInKBClassifier();

    @Parameters
    public static Collection<Object[]> data() {
        List<Object[]> testConfigs = new ArrayList<Object[]>();
        testConfigs.add(new Object[] { null, false });
        testConfigs.add(new Object[] { "http://dbpedia.org/resource/China", true });
        testConfigs.add(new Object[] { "http://en.dbpedia.org/resource/China", true });
        testConfigs.add(new Object[] { "http://dbpedia.org/resource/People's_Republic_of_China", true });
        testConfigs.add(new Object[] { "http://notExisting.wikipedia.org/wiki/China", false });
        testConfigs.add(new Object[] { "http://aksw.org/fake/de.dbpedia.org/resource/China", false });
        return testConfigs;
    }

    private String uri;
    private boolean expectedInKb;

    public FilteringWhileLoadingTest(String uri, boolean expectedInKb) {
        this.uri = uri;
        this.expectedInKb = expectedInKb;
    }

    @Test
    public void run() {
        Meaning meaning = new Annotation(uri);
        // sameAs retrieval
        SameAsRetrieverSingleton4Tests.getInstance().addSameURIs(meaning.getUris());
        // check for URI existance
        EntityCheckerManagerSingleton4Tests.getInstance().checkMeanings(Arrays.asList(meaning));
        // check whether the set contains URIs of the KB
        Assert.assertEquals(expectedInKb, GLOBAL_CLASSIFIER.containsKBUri(meaning.getUris()));
    }
}
