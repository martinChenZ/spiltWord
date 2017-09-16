package baseTool;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import constantDic.WordPos;

import dataCell.SentenceEntity;
import dataCell.SentenceToken;
import dataCell.Word;
import dataCell.WordEntity;
import dataStructure.TernarySearchTrie;
//分词器
public class Segmenter {       
    private static final double minValue =  -100000000.0;
    private static final double lambda1 = 0.5;  //一元概率权重
    private static final double lambda2 = 0.5;  //二元概率权重
    static{
        TernarySearchTrie.create();
    }
    public static List<WordEntity> splite(String sentence){
        List<WordEntity> wordEntityList = new ArrayList<WordEntity>();
        SentenceEntity entity = TernarySearchTrie.matchAll(sentence);         
        List<SentenceToken> tokenList = entity.getSentenceToken(); 
        SentenceEntity.TokenSet[] tokenSet =entity.getTokenSet();
        int sentenceLength = sentence.length();
        double endNodeProb=minValue;
        SentenceToken endToken=null;
        //算出每个词的最佳前驱词
        for(SentenceToken currentToken:tokenList){
            double candidateMaxProb = minValue;
            for(SentenceToken candidateToken:tokenSet[currentToken.getStart()]){
                if(candidateToken==null){
                    continue;
                }
                double prob = transProb(candidateToken, currentToken)+candidateToken.getAccProb();
                if(prob>candidateMaxProb){
                    candidateMaxProb=prob;
                    currentToken.setAccProb(prob);
                    currentToken.setBestPreWord(candidateToken);
                }
            }
            //找出最佳的最后节点
            if(currentToken.getEnd()==sentenceLength){
                if(currentToken.getAccProb()>endNodeProb){
                    endNodeProb=currentToken.getAccProb();
                    endToken=currentToken;
                } 
            }
        }
        SentenceToken indexToken=endToken;
        while(true){
            if(indexToken.getStart()<0){
                break;
            }
            wordEntityList.add(indexToken.getEntity());
            indexToken=indexToken.getBestPreWord();
        }
        Collections.reverse(wordEntityList);
        return wordEntityList;
    }
    public static List<Word> spliteSentence(String sentence){
        List<Word> wordList = new ArrayList<Word>();
        SentenceEntity entity = TernarySearchTrie.matchAll(sentence);         
        List<SentenceToken> tokenList = entity.getSentenceToken(); 
        SentenceEntity.TokenSet[] tokenSet =entity.getTokenSet();
        int sentenceLength = sentence.length();
        double endNodeProb=minValue;
        SentenceToken endToken=null;
        //算出每个词的最佳前驱词
        for(SentenceToken currentToken:tokenList){
            double candidateMaxProb = minValue;
            for(SentenceToken candidateToken:tokenSet[currentToken.getStart()]){
                if(candidateToken==null){
                    continue;
                }
                double prob = transProb(candidateToken, currentToken)+candidateToken.getAccProb();
                if(prob>candidateMaxProb){
                    candidateMaxProb=prob;
                    currentToken.setAccProb(prob);
                    currentToken.setBestPreWord(candidateToken);
                }
            }
            //找出最佳的最后节点
            if(currentToken.getEnd()==sentenceLength){
                if(currentToken.getAccProb()>endNodeProb){
                    endNodeProb=currentToken.getAccProb();
                    endToken=currentToken;
                } 
            }
        }
        SentenceToken indexToken=endToken;
        while(true){
            if(indexToken.getStart()<0){
                break;
            }
            Word word = new Word();
            word.setWord(indexToken.getEntity().getWord());
            word.setPos(indexToken.getEntity().getPos());
            word.setPosName(WordPos.getPosName(indexToken.getEntity().getPos()));
            wordList.add(word);
            indexToken=indexToken.getBestPreWord();
        }
        Collections.reverse(wordList);
        return wordList;
    }
    // 前后两个词的转移概率
    private static double transProb(SentenceToken candidateToken, SentenceToken currentToken) {
        double biProb;  //二元转移概率
        int preLen = candidateToken.getEntity().getWord().length();
        int nextLen = currentToken.getEntity().getWord().length();
        if (preLen < nextLen) {
            biProb = 0.2; 
        } else if (preLen == nextLen) {
            biProb = 0.1;  
        } else {
            biProb = 0.0001; 
        }

        return lambda1 * candidateToken.getEntity().getLogProb() + lambda2 * Math.log(biProb);
        // return prevWord.logProb;
    }
    
    public static void main(String[] args){
        //String sentence="最近，随着深度学习的兴起，神经网络语言模型也变得火热。用神经网络训练语言模型的经典之作，要数Bengio等人发表的《A Neural Probabilistic Language Model》,SCANV网址安全中心是一个综合性的网址安全服务平台。通过网址安全中心，用户可以方便的查询到要访问的网址是否存在恶意行为，同时可以在SCANV中在线举报违法恶意网站你好,我想使用ArrayList's aa制juy n80制造算法,基因工程,即直接访问.态度很好，槑讲解很123全面大学生活动中心大学生活咬死猎人的狗,上海自来水来自海上";
/*        List<WordEntity> list = splite(sentence);
        for(WordEntity entity:list){
            System.err.println(entity);
        }*/
        String sentence="我是习近平时喜欢打球".trim();
        //String sentence="有任何疑问以及突发情况";
        System.err.println(sentence);
        List<Word> list2 = spliteSentence(sentence);
        for(Word entity:list2){
            System.err.println(entity);
        }
    }
}