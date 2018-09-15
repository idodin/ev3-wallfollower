package ca.mcgill.ecse211.wallfollowing;

import lejos.hardware.motor.EV3LargeRegulatedMotor;

public class PController implements UltrasonicController {

  /* Constants */
  private static final int MOTOR_SPEED = 200;
  private static final int FILTER_OUT = 20;

  private final int bandCenter;
  private final int bandWidth;
  private final int GAIN = 2;
  private int diff;
  private int distError;
  private int distance;
  private int filterControl;

  public PController(int bandCenter, int bandwidth) {
    this.bandCenter = bandCenter;
    this.bandWidth = bandwidth;
    this.filterControl = 0;

    WallFollowingLab.leftMotor.setSpeed(MOTOR_SPEED); // Initalize motor rolling forward
    WallFollowingLab.rightMotor.setSpeed(MOTOR_SPEED);
    WallFollowingLab.leftMotor.forward();
    WallFollowingLab.rightMotor.forward();
  }

  @Override
  public void processUSData(int distance) {

    // rudimentary filter - toss out invalid samples corresponding to null
    // signal.
    // (n.b. this was not included in the Bang-bang controller, but easily
    // could have).
    //
    if (distance >= 255 && filterControl < FILTER_OUT) {
      // bad value, do not set the distance var, however do increment the
      // filter value
      filterControl++;
    } else if (distance >= 255) {
      // We have repeated large values, so there must actually be nothing
      // there: leave the distance alone
      this.distance = distance;
    } else {
      // distance went below 255: reset filter and leave
      // distance alone.
      filterControl = 0;
      this.distance = distance;
    }

    distError = distance - bandCenter;
    diff = Math.abs(distError) * GAIN;
    
    if (Math.abs(distError) <= bandWidth) {
    	WallFollowingLab.leftMotor.setSpeed(MOTOR_SPEED);
    	WallFollowingLab.rightMotor.setSpeed(MOTOR_SPEED);
        WallFollowingLab.leftMotor.forward();
        WallFollowingLab.rightMotor.forward();
        System.out.println("forward");
    }
    else if (distError > 0) {
    	WallFollowingLab.leftMotor.setSpeed(100);
    	WallFollowingLab.rightMotor.setSpeed(Math.min(MOTOR_SPEED+diff, 350));
        WallFollowingLab.leftMotor.forward();
        WallFollowingLab.rightMotor.forward();
        System.out.println("left");
    }
    else if (distError < 0 && distError > -10) {
    	WallFollowingLab.leftMotor.setSpeed(Math.min(MOTOR_SPEED+diff, 350));
    	WallFollowingLab.rightMotor.setSpeed(60);
        WallFollowingLab.leftMotor.forward();
        WallFollowingLab.rightMotor.forward();
        System.out.println("right");
    }
    else if (distError < 0 && distError <= -10) {
    	WallFollowingLab.leftMotor.setSpeed(MOTOR_SPEED);
    	WallFollowingLab.rightMotor.setSpeed(MOTOR_SPEED);
        WallFollowingLab.leftMotor.backward();
        WallFollowingLab.rightMotor.backward();
        System.out.println("back");
    }
    
    
    
    
    // TODO: process a movement based on the us distance passed in (P style)
    
  }


  @Override
  public int readUSDistance() {
    return this.distance;
  }

}
