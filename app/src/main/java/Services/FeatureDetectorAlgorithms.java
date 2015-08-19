package Services;

import android.graphics.Bitmap;

import org.opencv.android.Utils;
import org.opencv.core.Mat;
import org.opencv.core.MatOfByte;
import org.opencv.core.MatOfDMatch;
import org.opencv.core.MatOfKeyPoint;
import org.opencv.core.MatOfPoint2f;
import org.opencv.core.Point;
import org.opencv.core.Scalar;
import org.opencv.features2d.DMatch;
import org.opencv.features2d.DescriptorExtractor;
import org.opencv.features2d.DescriptorMatcher;
import org.opencv.features2d.FeatureDetector;
import org.opencv.features2d.Features2d;
import org.opencv.features2d.KeyPoint;
import org.opencv.highgui.Highgui;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Created by guilhermecardoso on 8/10/15.
 */
public class FeatureDetectorAlgorithms {
//
//    public static Bitmap ORB(String firstPath, String secondPath){
//        FeatureDetector detector = FeatureDetector.create(FeatureDetector.ORB);
//        DescriptorExtractor descriptor = DescriptorExtractor.create(DescriptorExtractor.ORB);;
//        DescriptorMatcher matcher = DescriptorMatcher.create(DescriptorMatcher.BRUTEFORCE_HAMMING);
//
//
//        //first image
//        Mat img1 = Highgui.imread(firstPath);
//        Mat descriptors1 = new Mat();
//        MatOfKeyPoint keypoints1 = new MatOfKeyPoint();
//
//        detector.detect(img1, keypoints1);
//        descriptor.compute(img1, keypoints1, descriptors1);
//
//        //second image
//        Mat img2 = Highgui.imread(secondPath);
//        Mat descriptors2 = new Mat();
//        MatOfKeyPoint keypoints2 = new MatOfKeyPoint();
//
//        detector.detect(img2, keypoints2);
//        descriptor.compute(img2, keypoints2, descriptors2);
//
//        //matcher should include 2 different image's descriptors
//        MatOfDMatch matches = new MatOfDMatch();
//        matcher.match(descriptors1,descriptors2,matches);
//
//        //feature and connection colors
//        Scalar RED = new Scalar(255,0,0);
//        Scalar GREEN = new Scalar(0,255,0);
//
//        //output image
//        Mat outputImg = new Mat();
//        MatOfByte drawnMatches = new MatOfByte();
//
//        //this will draw all matches, works fine
//        Features2d.drawMatches(img1, keypoints1, img2, keypoints2, matches,
//                outputImg, GREEN, RED, drawnMatches, Features2d.NOT_DRAW_SINGLE_POINTS);
//
//        Bitmap imageMatched = Bitmap.createBitmap(outputImg.cols(), outputImg.rows(), Bitmap.Config.RGB_565);//need to save bitmap
//        Utils.matToBitmap(outputImg, imageMatched);
//        return imageMatched;
//    }

    private static MatOfPoint2f convertMatOfKeypoint2MatOfPoint2f(MatOfKeyPoint keypoints){
        if (keypoints != null){
            //First convert the keypoints to array format, so we can use an array list to dynamically
            // add on the new array and then create a MatOfPoint2f
            KeyPoint keypointsToConvert[] =  keypoints.toArray();

            Point pointsIntermediate[] = new Point[keypointsToConvert.length];

            for (int i = 0; i < keypointsToConvert.length; i++){
                pointsIntermediate[i] = keypointsToConvert[i].pt;
            }

            MatOfPoint2f pointsConverted = new MatOfPoint2f();
            pointsConverted.fromArray(pointsIntermediate);

            return pointsConverted;
        }
        return null;
    }

    public static Bitmap ORB(String firstPath, String secondPath){
        FeatureDetector detector = FeatureDetector.create(FeatureDetector.ORB);
        DescriptorExtractor descriptor = DescriptorExtractor.create(DescriptorExtractor.ORB);;
        DescriptorMatcher matcher = DescriptorMatcher.create(DescriptorMatcher.BRUTEFORCE_HAMMING);


        //first image
        Mat img1 = Highgui.imread(firstPath);
        Mat descriptors1 = new Mat();
        MatOfKeyPoint keypoints1 = new MatOfKeyPoint();
        MatOfKeyPoint keypoints1new = new MatOfKeyPoint();

        detector.detect(img1, keypoints1);
        descriptor.compute(img1, keypoints1, descriptors1);

        //second image
        Mat img2 = Highgui.imread(secondPath);
        Mat descriptors2 = new Mat();
        MatOfKeyPoint keypoints2 = new MatOfKeyPoint();
        MatOfKeyPoint keypoints2new = new MatOfKeyPoint();

        detector.detect(img2, keypoints2);
        descriptor.compute(img2, keypoints2, descriptors2);

        //matcher should include 2 different image's descriptors
        //Also making 2 matchers to filter the matches
        MatOfDMatch matches12 = new MatOfDMatch();
        MatOfDMatch matches21 = new MatOfDMatch();
        matcher.match(descriptors1, descriptors2, matches12);
        matcher.match(descriptors2, descriptors1, matches21);

        //Needs matches in DMatch format
        DMatch[] matchesConverted12 = matches12.toArray();
        DMatch[] matchesConverted21 = matches21.toArray();
        ArrayList<DMatch> filteredMatches12 = new ArrayList<DMatch>();

        //Now need to convert the MatOfKeyPoint
        MatOfPoint2f keypointsConverted1 = convertMatOfKeypoint2MatOfPoint2f(keypoints1);
        MatOfPoint2f keypointsConverted2 = convertMatOfKeypoint2MatOfPoint2f(keypoints2);

        //Filtering matches
        for (int i = 0; i < matchesConverted12.length; i++){
            DMatch forward = matchesConverted12[i];
            DMatch backward = matchesConverted21[forward.trainIdx];
            if( backward.trainIdx == forward.queryIdx )
                filteredMatches12.add(forward);
        }

        Collections.sort(filteredMatches12, new Comparator<DMatch>() {
            @Override
            public int compare(DMatch o1, DMatch o2) {
                if (o1.distance < o2.distance)
                    return -1;
                if (o1.distance > o2.distance)
                    return 1;
                return 0;
            }
        });
        int min = Math.min(matchesConverted12.length, matchesConverted21.length);
        if(filteredMatches12.size() > min){
            ArrayList<DMatch> aux = new ArrayList<DMatch>();
            for (int i = 0; i < min; i++){
                aux.add(filteredMatches12.get(i));
            }
            filteredMatches12 = aux;
        }

        DMatch matc[] = new DMatch[filteredMatches12.size()];
        for(int i = 0; i < filteredMatches12.size();i++){
            matc[i] = filteredMatches12.get(i);
        }

        MatOfDMatch matches = new MatOfDMatch(matc);

        //Find now the Fundamental Matrix
        //Mat fundamentalMatrix = Calib3d.findFundamentalMat(keypointsConverted1,keypointsConverted2);


        //Conversao do MatofDMatch para MatofPoint2f
        MatOfPoint2f prevPts = getMatOfPoint2fFromDMatchesTrain(matches,keypoints1);
        MatOfPoint2f nextPts = getMatOfPoint2fFromDMatchesQuery(matches, keypoints2);

        //Calculo da Matriz Fundamental
        //Mat fundamental_matrix = Calib3d.findFundamentalMat(
          //      nextPts, prevPts, Calib3d.FM_RANSAC, 3, 0.99);

        //Calib3d.correctMatches(fundamental_matrix,keypoints1,keypoints2,keypoints1,keypoints2);

        //feature and connection colors
        Scalar RED = new Scalar(255,0,0);
        Scalar GREEN = new Scalar(0,255,0);

        //output image
        Mat outputImg = new Mat();
        MatOfByte drawnMatches = new MatOfByte();

        //outputImg = fundamental_matrix;

        //this will draw all matches, works fine
        Features2d.drawMatches(img1, keypoints1, img2, keypoints2, matches,
                outputImg, GREEN, RED, drawnMatches, Features2d.NOT_DRAW_SINGLE_POINTS);

        Bitmap imageMatched = Bitmap.createBitmap(outputImg.cols(), outputImg.rows(), Bitmap.Config.RGB_565);//need to save bitmap
        Utils.matToBitmap(outputImg, imageMatched);
        return imageMatched;
    }

    static private MatOfPoint2f getMatOfPoint2fFromDMatchesTrain(MatOfDMatch matches2,
                                                                 MatOfKeyPoint prevKP2) {
        DMatch dm[] = matches2.toArray();
        List<Point> lp1 = new ArrayList<Point>(dm.length);
        KeyPoint tkp[] = prevKP2.toArray();
        for (int i = 0; i < dm.length; i++) {
            DMatch dmm = dm[i];
            if (dmm.trainIdx < tkp.length)
                lp1.add(tkp[dmm.trainIdx].pt);
        }
        return new MatOfPoint2f(lp1.toArray(new Point[0]));
    }

    static private MatOfPoint2f getMatOfPoint2fFromDMatchesQuery(MatOfDMatch matches2,
                                                                 MatOfKeyPoint actKP2) {
        DMatch dm[] = matches2.toArray();
        List<Point> lp2 = new ArrayList<Point>(dm.length);
        KeyPoint qkp[] = actKP2.toArray();
        for (int i = 0; i < dm.length; i++) {
            DMatch dmm = dm[i];
            if (dmm.queryIdx < qkp.length)
                lp2.add(qkp[dmm.queryIdx].pt);
        }
        return new MatOfPoint2f(lp2.toArray(new Point[0]));
    }



}
