package com.cokus.audiocanvaswave.util;

import com.musicg.fingerprint.FingerprintSimilarity;
import com.musicg.wave.Wave;

import java.io.File;

/**
 * Created by cokus on 2016/9/1.
 */
public class MusicSimilarityUtil {

    /**
     * 比较两文件的获取相似度
     * @param oriFilePath 原始文件路径
     * @param comFilePath 比较的文件路径
     * @return 相识度
     */
    public static float getSimByCompareFile(String oriFilePath,String comFilePath){
        Wave oriWave = new Wave(oriFilePath);
        Wave comWave = new Wave(comFilePath);
        FingerprintSimilarity fps = oriWave.getFingerprintSimilarity(comWave);
        float sim = fps.getSimilarity();
        return sim;
    }

    /**
     * 获取音频和标准文件的得分
     * @param oriFilePath 原始文件路径
     * @param comFilePath 比较的文件路径
     * @return 得分
     */
    public static float getScoreByCompareFile(String oriFilePath,String comFilePath){
        Wave oriWave = new Wave(oriFilePath);
        Wave comWave = new Wave(comFilePath);
        FingerprintSimilarity fps = oriWave.getFingerprintSimilarity(comWave);
        float score = fps.getScore();
        return score;
    }


}
