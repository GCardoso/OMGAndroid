package Services;

import android.graphics.Bitmap;
import android.util.Log;

import org.opencv.android.Utils;
import org.opencv.calib3d.Calib3d;
import org.opencv.core.CvException;
import org.opencv.core.CvType;
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
import java.util.LinkedList;
import java.util.List;

/**
 * Created by guilhermecardoso on 8/10/15.
 */
public class FeatureDetectorAlgorithms {
    private static String TAG = "FeatureDetectorAlgorithms";

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

    public static Bitmap ORB(Mat firstPath, Mat secondPath){
        FeatureDetector detector = FeatureDetector.create(FeatureDetector.ORB);
        DescriptorExtractor descriptor = DescriptorExtractor.create(DescriptorExtractor.ORB);;
        DescriptorMatcher matcher = DescriptorMatcher.create(DescriptorMatcher.BRUTEFORCE_HAMMING);


        //first image

        Mat img1 = //                Highgui.imread(firstPath);
                firstPath;
        Mat descriptors1 = new Mat();
        MatOfKeyPoint keypoints_object = new MatOfKeyPoint();

        detector.detect(img_object, keypoints_object);
        descriptor.compute(img_object, keypoints_object, descriptors1);

        //second image
        Mat img2 = //Highgui.imread(secondPath);
                secondPath;

        Mat descriptors2 = new Mat();
        MatOfKeyPoint keypoints_scene = new MatOfKeyPoint();

        detector.detect(img_scene, keypoints_scene);
        descriptor.compute(img_scene, keypoints_scene, descriptors2);

        Log.i("CAMERAFRAME", "Types desc before : " + descriptors1.type() + " , " + descriptors2.type() + ".");
        Log.i("CAMERAFRAME", "Rows desc before " + descriptors1.rows() + " m1 rows -> " + descriptors2.rows() + " m2 row");
        Log.i("CAMERAFRAME", "Cols desc before " + descriptors1.cols() + " m1 rows -> " + descriptors2.cols() + " m2 row");
        descriptors1.convertTo(descriptors1, CvType.CV_8U);
        descriptors2.convertTo(descriptors2, CvType.CV_8U);

        Log.i("CAMERAFRAME", "Types: " + img1.type() + " , " + img2.type() + ".");
        Log.i("CAMERAFRAME", "Types desc after : " + descriptors1.type() + " , " + descriptors2.type() + ".");
        Log.i("CAMERAFRAME", "Rows desc after " + descriptors1.rows() + " m1 rows -> " + descriptors2.rows() + " m2 row");
        Log.i("CAMERAFRAME", "Cols desc after " + descriptors1.cols() + " m1 rows -> " + descriptors2.cols() + " m2 row");

        //matcher should include 2 different image's descriptors
        MatOfDMatch matches = new MatOfDMatch();
        matches.convertTo(matches, CvType.CV_32F);
        Log.i("CAMERAFRAME", "Type matches: " + matches.type());
        try {
            matcher.match(descriptors1,descriptors2,matches);
        }catch (CvException e){
			//Capturar Excecao de Matriz de Descritores vazia
            if (descriptors1.cols()==0 || descriptors2.cols()==0 || descriptors1.rows()==0 || descriptors2.rows()==0){
                Log.e("Matcher","Descriptors have 0 col or rows : d1 : "+ descriptors1.cols() + " x " + descriptors1.rows() + ", d2: " +descriptors2.cols() + " x " + descriptors2.rows() + " ." );
                return null;
            }
        }

        List<DMatch> matcheslist = matches.toList();

        double maxDist = 0.0;
        double minDist = 100.0;

        for (int i = 0; i < keypoints_object.rows(); i++){
            double dist = matcheslist.get(i).distance;
            if (dist < minDist){
                minDist = dist;
            }

            if (dist > maxDist){
                maxDist = dist;
            }
        }

        //Log.i(TAG, "Max = " + maxDist + " Min = " + minDist);

        LinkedList<DMatch> goodMatches = new LinkedList<DMatch>();
        for (int i = 0; i < descriptors1.rows(); i++){
            if (matcheslist.get(i).distance < 3 * minDist){
                goodMatches.addLast(matcheslist.get(i));
            }
        }

        MatOfDMatch gm = new MatOfDMatch();
        gm.fromList(goodMatches);

        //feature and connection colors
        Scalar RED = new Scalar(255,0,0);
        Scalar GREEN = new Scalar(0,255,0);

        //now find the homography matrix
        List<KeyPoint> obj_keypont = keypoints_object.toList();
        List<KeyPoint> scene_keypont = keypoints_scene.toList();

        LinkedList<Point> obj_pointsList = new LinkedList<Point>();
        LinkedList<Point> scene_pointsList = new LinkedList<Point>();

        for (int i = 0; i < goodMatches.size(); i++){
            obj_pointsList.addLast(obj_keypont.get(goodMatches.get(i).queryIdx).pt);
            scene_pointsList.addLast(scene_keypont.get(goodMatches.get(i).trainIdx).pt);
        }

        MatOfPoint2f obj = new MatOfPoint2f();
        obj.fromList(obj_pointsList);
        MatOfPoint2f scene = new MatOfPoint2f();
        scene.fromList(scene_pointsList);
        Mat inliners = new Mat();
        Mat homography = Calib3d.findHomography(obj,scene,Calib3d.RANSAC,1,inliners);
        Mat obj_corners = new Mat(4,1, CvType.CV_32FC2);
        Mat scene_corners = new Mat(4,1,CvType.CV_32FC2);


        //Cleaning not good matches
        Mat outputImg = new Mat();
        List<KeyPoint> listOfGoodKeypointsObj = keypoints_object.toList();
        List<KeyPoint> listOfGoodKeypointsScene = keypoints_scene.toList();
        for (int i = 0; i < inliners.rows(); i++){
            StringBuilder sb = new StringBuilder("");
            for (int j = 0; j < inliners.cols(); j++){
                double[] indexes = inliners.get(i,j);
                for (int k = 0; k < indexes.length; k++){
                    sb.append(indexes[k] + " ");
                    if (indexes[k] <= 0){
                        listOfGoodKeypointsObj.remove(keypoints_object.get(i,j));
                        listOfGoodKeypointsScene.remove(keypoints_scene.get(i,j));
                    }
                }
                Log.i(TAG,"inliners(" + i + "," + j + ") -> " + sb.toString());
//                Log.i(TAG,"inliners size = " + inliners.size());
//                Log.i(TAG,"obj size = " + obj.size());
//                Log.i(TAG,"scene size = " + scene.size());
            }
        }

        //output image
        //this will draw all matches, works fine
        //Features2d.drawMatches(img_object, keypoints_object, img_scene, keypoints_scene, gm,
        //        outputImg, GREEN, RED, drawnMatches, Features2d.NOT_DRAW_SINGLE_POINTS);
        //Features2d.drawMatches(img_object,keypoints_object,img_scene,keypoints_scene,gm,outputImg,GREEN,RED,new MatOfByte(inliners),Features2d.NOT_DRAW_SINGLE_POINTS);
        MatOfKeyPoint keypoints1 = new MatOfKeyPoint();
        keypoints1.fromList(listOfGoodKeypointsObj);
        MatOfKeyPoint keypoints2 = new MatOfKeyPoint();
        keypoints2.fromList(listOfGoodKeypointsScene);
        Features2d.drawMatches(img_object, keypoints1, img_scene, keypoints2, gm, outputImg, GREEN, RED, new MatOfByte(inliners), Features2d.NOT_DRAW_SINGLE_POINTS);

//        Core.line(img_object, new Point(), new Point(), new Scalar(0, 255, 0), 4);
//        Core.line(img_object, new Point(), new Point(), new Scalar(0,255,0), 4);
//        Core.line(img_object, new Point(), new Point(), new Scalar(0, 255, 0), 4);
//        Core.line(img_object, new Point(), new Point(), new Scalar(0, 255, 0), 4);

        Bitmap imageMatched = Bitmap.createBitmap(outputImg.cols(), outputImg.rows(), Bitmap.Config.RGB_565);//need to save bitmap
        Utils.matToBitmap(outputImg, imageMatched);
        return imageMatched;
    }

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
//        //Also making 2 matchers to filter the matches
//        MatOfDMatch matches12 = new MatOfDMatch();
//        MatOfDMatch matches21 = new MatOfDMatch();
//        matcher.match(descriptors1, descriptors2, matches12);
//        matcher.match(descriptors2, descriptors1, matches21);
//
//        //Needs matches in DMatch format
//        DMatch[] matchesConverted12 = matches12.toArray();
//        DMatch[] matchesConverted21 = matches21.toArray();
//        ArrayList<DMatch> filteredMatches12 = new ArrayList<DMatch>();
//
//        //Now need to convert the MatOfKeyPoint
//        MatOfPoint2f keypointsConverted1 = convertMatOfKeypoint2MatOfPoint2f(keypoints1);
//        MatOfPoint2f keypointsConverted2 = convertMatOfKeypoint2MatOfPoint2f(keypoints2);
//
//        Log.i(TAG,keypointsConverted1.toString());
//        Log.i(TAG,keypointsConverted2.toString());
//
////        //Filtering matches, cross check
////        for (int i = 0; i < matchesConverted12.length; i++){
////            DMatch forward = matchesConverted12[i];
////            DMatch backward = matchesConverted21[forward.trainIdx];
////            if( backward.trainIdx == forward.queryIdx )
////                filteredMatches12.add(forward);
////        }
//
////        Collections.sort(filteredMatches12, new Comparator<DMatch>() {
////            @Override
////            public int compare(DMatch o1, DMatch o2) {
////                if (o1.distance < o2.distance)
////                    return -1;
////                if (o1.distance > o2.distance)
////                    return 1;
////                return 0;
////            }
////        });
////        int min = Math.min(matchesConverted12.length, matchesConverted21.length);
////        if(filteredMatches12.size() > min){
////            ArrayList<DMatch> aux = new ArrayList<DMatch>();
////            for (int i = 0; i < min; i++){
////                aux.add(filteredMatches12.get(i));
////            }
////            filteredMatches12 = aux;
////        }
//
//        DMatch matc[] = new DMatch[filteredMatches12.size()];
//        for(int i = 0; i < filteredMatches12.size();i++){
//            matc[i] = filteredMatches12.get(i);
//        }
//
//        //MatOfDMatch matches = new MatOfDMatch(matc);
//        matches =
//        //Find now the Fundamental Matrix
//        Log.i(TAG,"keypointsConverted1 size = " + keypointsConverted1.toArray().length);
//        Log.i(TAG,"keypointsConverted2 size = " + keypointsConverted2.toArray().length);
//        //Mat fundamentalMatrix = Calib3d.findFundamentalMat(keypointsConverted1, keypointsConverted2);
//
//
//        //Conversao do MatofDMatch para MatofPoint2f
//        MatOfPoint2f prevPts = getMatOfPoint2fFromDMatchesTrain(matches,keypoints1);
//        MatOfPoint2f nextPts = getMatOfPoint2fFromDMatchesQuery(matches, keypoints2);
//
//        //Calculo da Matriz Fundamental
//        Mat fundamental_matrix = Calib3d.findFundamentalMat(
//                nextPts, prevPts, Calib3d.FM_RANSAC, 3, 0.99);
//
//        //Calib3d.correctMatches(fundamental_matrix,keypoints1,keypoints2,keypoints1,keypoints2);
//
//        //feature and connection colors
//        Scalar RED = new Scalar(255,0,0);
//        Scalar GREEN = new Scalar(0,255,0);
//
//        //output image
//        Mat outputImg = new Mat();
//        MatOfByte drawnMatches = new MatOfByte();
//
//        //outputImg = fundamental_matrix;
//
//        //this will draw all matches, works fine
//        Features2d.drawMatches(img1, keypoints1, img2, keypoints2, matches,
//                outputImg, GREEN, RED, drawnMatches, Features2d.NOT_DRAW_SINGLE_POINTS);
//
//        Bitmap imageMatched = Bitmap.createBitmap(outputImg.cols(), outputImg.rows(), Bitmap.Config.RGB_565);//need to save bitmap
//        Utils.matToBitmap(outputImg, imageMatched);
//        return imageMatched;
//    }

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
