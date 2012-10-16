============================================================================

    Twitter Part-of-Speech Annotated Data 
    Copyright (C) 2011-2012
    Kevin Gimpel, Nathan Schneider, Brendan O'Connor, Dipanjan Das, Daniel
    Mills, Jacob Eisenstein, Michael Heilman, Dani Yogatama, Jeffrey Flanigan,
    and Noah A. Smith 
    Language Technologies Institute, Carnegie Mellon University

    http://www.ark.cs.cmu.edu/TweetNLP

    This data is made available under the terms of the Creative Commons
    Attribution 3.0 Unported license ("CC-BY"):
    http://creativecommons.org/licenses/by/3.0/

=============================================================================

  Description:

    This is release v0.2.1 of a data set of tweets manually annotated with part-of-speech 
    tags. The annotated data is divided into three files: 
      "train" (1000 tweets),
      "dev" (327 tweets), and
      "test" (500 tweets). 
    Each file contains one token (with its tag separated by a tab) per line, and a blank 
    line to indicate a tweet boundary. See Gimpel et al. (2011) for information on the tagset.

    We attempted to anonymize all Twitter usernames within tweets, using a
    salted hash mapping Twitter usernames to unique identifiers.

  Contact:

    Please contact Brendan O'Connor (brenocon@cs.cmu.edu, http://brenocon.com)
    with any questions about this release.

  Changes:

    Version 0.2.1 (2012-08-01): License changed from GPL to CC-BY.

    Version 0.2 (2011-08-15): Based on an improved Twitter tokenizer.  After the new
    tokenizer was run, tweets with differing tokenizations were reannotated
    following the same guidelines as the initial release.

    Version 0.1 (2011-04-26): First release.

  References:

    The following paper describes this dataset.  If you use this data in a
    research publication, we ask that you cite it.

    Kevin Gimpel, Nathan Schneider, Brendan O'Connor, Dipanjan Das, Daniel
    Mills, Jacob Eisenstein, Michael Heilman, Dani Yogatama, Jeffrey Flanigan,
    and Noah A. Smith.
    Part-of-Speech Tagging for Twitter: Annotation, Features, and Experiments.
    In Proceedings of the Annual Meeting of the Association for Computational
    Linguistics, companion volume, Portland, OR, June 2011.

