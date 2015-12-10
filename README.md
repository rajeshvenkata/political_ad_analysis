# political_ad_analysis
The aim of project is to analyze the 2016 political ads and categorize them according to the tone of the video. Each video is classified as attacking or defending and addresser, addressee of the video can be determined and a relatioship is logged among them.

for example: A video is attacking and A,B are addresser, addressee respectively. Relation of A attacking B is logged.

Algorithm has three main steps
1)Converting videos to text using google web speech API. Created a web application to use this service
2)Process the converted video text by removing stop words and performign Natural Language Processing.
3)Classifying the videos. This has been achieved by determining Extended Jaccard Similarity index between each video and dictionaries (attacking & defending)
