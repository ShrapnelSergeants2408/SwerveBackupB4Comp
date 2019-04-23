package org.usfirst.frc.team2408.robot;

import edu.wpi.first.wpilibj.AnalogInput;
import edu.wpi.first.wpilibj.IterativeRobot;
import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.Servo;
import edu.wpi.first.wpilibj.Spark;
import edu.wpi.first.wpilibj.SpeedController;
import edu.wpi.first.wpilibj.Talon;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

/**
 * The VM is configured to automatically run this class, and to call the
 * functions corresponding to each mode, as described in the IterativeRobot
 * documentation. If you change the name of this class or the package after
 * creating this project, you must also update the manifest file in the resource
 * directory.
 */
public class Robot extends IterativeRobot {
	final String defaultAuto = "Default";
	final String customAuto = "My Auto";
	String autoSelected;
	SendableChooser<String> chooser = new SendableChooser<>();
	
	SpeedController FrontRightTurn = new Spark(1);
	SpeedController FrontLeftTurn = new Spark(0);
	SpeedController BackRightTurn = new Spark(3);
	SpeedController BackLeftTurn = new Spark(2);
	
	SpeedController FrontRightDrive = new Spark(5);
	SpeedController FrontLeftDrive = new Spark(4);
	SpeedController BackRightDrive = new Spark(17);
	SpeedController BackLeftDrive = new Spark(6);
	
	AnalogInput EncoderFrontRight = new AnalogInput(1);
	AnalogInput EncoderFrontLeft = new AnalogInput(0);
	AnalogInput EncoderBackRight = new AnalogInput(3);
	AnalogInput EncoderBackLeft = new AnalogInput(2);
	
	Servo DoorLeft = new Servo(10);
	Servo DoorRight = new Servo(11);
	Servo DoorShooter = new Servo(12);
	Servo Agitator = new Servo(13);
	
	SpeedController Shooter = new Talon(9);
	SpeedController Winch = new Talon(8);
	
	Joystick stick = new Joystick(0); 
	Joystick OpStick = new Joystick(1);
	
	Timer myTimer = new Timer();

	/**
	 * This function is run when the robot is first started up and should be
	 * used for any initialization code.
	 */
	@Override
	public void robotInit() {
		chooser.addDefault("Default Auto", defaultAuto);
		chooser.addObject("My Auto", customAuto);
		SmartDashboard.putData("Auto choices", chooser);
		
		// Set servos to desired initial positions
		Agitator.setAngle(170);
		DoorShooter.setAngle(90);
		DoorLeft.setAngle(0);
		DoorRight.setAngle(0);
	}

	/**
	 * This autonomous (along with the chooser code above) shows how to select
	 * between different autonomous modes using the dashboard. The sendable
	 * chooser code works with the Java SmartDashboard. If you prefer the
	 * LabVIEW Dashboard, remove all of the chooser code and uncomment the
	 * getString line to get the auto name from the text box below the Gyro
	 *
	 * You can add additional auto modes by adding additional comparisons to the
	 * switch structure below with additional strings. If using the
	 * SendableChooser make sure to add them to the chooser code above as well.
	 */
	@Override
	public void autonomousInit() {
		autoSelected = chooser.getSelected();
		// autoSelected = SmartDashboard.getString("Auto Selector",
		// defaultAuto);
		System.out.println("Auto selected: " + autoSelected);
		
		// Reset timer to 0 seconds
		myTimer.reset();
		// Start timer
		myTimer.start();
		
		
	}

	/**
	 * This function is called periodically during autonomous
	 */
	@Override
	public void autonomousPeriodic() {
		switch (autoSelected) {
		case customAuto:
			// Put custom auto code here
			break;
		case defaultAuto:
		default:
			// Put default auto code here
			double dspeed;
			double steer;
			double diffspeed;
			boolean mode;
			// If time is less than 1 second, stay put
			if(myTimer.get() < 1.0){ 
				dspeed = 0.0;
				steer = 0.0;
				diffspeed = 0.0;
				mode = false;
				SwerveDrive(dspeed, steer, diffspeed, mode);
			}
			// If time is less than 3 second, drive straight forward
			else if(myTimer.get() < 3.0) {
				dspeed = 0.5;
				steer = 0.0;
				diffspeed = 0.0;
				mode = false;
				SwerveDrive(dspeed, steer, diffspeed, mode);
				}
			// stop
			else {
				dspeed = 0.0;
				steer = 0.0;
				diffspeed = 0.0;
				mode = false;
				SwerveDrive(dspeed, steer, diffspeed, mode);			
			}
				
			
			break;
		}
	}

	/**
	 * This function is called periodically during test mode
	 */
	@Override
	public void testPeriodic() {
	}

	/**
	 * This function is called periodically during operator control
	 */
	@Override
	public void teleopPeriodic() {
	
		// Get driver joystick input values
		double LeftYstick = stick.getRawAxis(1);
		double LeftXstick = stick.getRawAxis(0);
		double RightXstick = stick.getRawAxis(3);
		
		// Apply some shaping to the drive power joystick input
		// to make it less sensitive for small inputs
		double LeftYstick_shape;
		if (LeftYstick > 0) {
			LeftYstick_shape = LeftYstick*LeftYstick;
		} else {
			LeftYstick_shape = -LeftYstick*LeftYstick;
		}
        
		//  Call Swerve Drive Function
		SwerveDrive(LeftYstick_shape, RightXstick, LeftXstick, stick.getRawButton(1));
	
		// Winch Motor Logic
		if (OpStick.getRawAxis(3) > 0) {
			Winch.set(1);
		} else {
			Winch.stopMotor();
		}
		
		//  Shooter Motor Logic
		if (OpStick.getRawAxis(2) > 0) {
			Shooter.set(1);
		} else {
			Shooter.stopMotor();
		}
	    
		//  Agitator Servo Logic
		if (OpStick.getRawButton(5)) {
			Agitator.setAngle(75);
		} else {
			Agitator.setAngle(170);
		}
		
		// Logic for Door Servo Feeding Shooter
		if (OpStick.getRawButton(6)) {
			DoorShooter.setAngle(0);
		} else {
			DoorShooter.setAngle(90);
		}
		
		// Gear Door Servos
		if (OpStick.getRawButton(1)) {
			DoorLeft.setAngle(90);
			DoorRight.setAngle(-90);
		} else {
			DoorLeft.setAngle(0);
			DoorRight.setAngle(0);
		}

	}
	
	/**
	 * This function implements the guts of the swerve drive logic
	 * Inputs are
	 *   drive speed (-1 to 1)
	 *   steer command (-1 to 1)
	 *   diff speed command (-1 to 1)
	 *   steering mode (0 = swerve, 1 = snake)
	 */
	public void SwerveDrive(double dspeed, double steer, double diffspeed, boolean mode) {
		
		double FrontLeftEq;
		double FrontRightEq;
		double BackLeftEq;
		double BackRightEq;
		
		double jsgain_front;
		double jsgain_back;
		
		double FrontLeftError;
		double FrontRightError;
		double BackLeftError;
		double BackRightError;
		
        //  Read steering position for each of the swerve modules
		double FrontRightAverage = EncoderFrontRight.getAverageVoltage();
		double FrontLeftAverage = EncoderFrontLeft.getAverageVoltage();
		double BackRightAverage = EncoderBackRight.getAverageVoltage();
		double BackLeftAverage = EncoderBackLeft.getAverageVoltage();
		
	    //  Determine if in straight swerve mode or in snake mode
		if (mode) {
			// if in snake mode, only use 45 degrees of steering
			// and steer back wheels in opposite direction as front wheels
			jsgain_front = 0.5*1.25;
			jsgain_back = -0.5*1.25;
		} else {
			// if in swerve mode, use 90 degrees 
			jsgain_front = 1.25;
			jsgain_back = 1.25;
		}
			// Set swerve module steering commands based on the right 
		    //  joystick command
		    // The last number should be set as the encoder reading when
		    //  modules are at zero degrees
			FrontLeftEq = jsgain_front*(steer) + 2.94;
			FrontRightEq = jsgain_front*(steer) + 1.44;
			BackLeftEq = jsgain_back*(steer) + 0.73;
			BackRightEq = jsgain_back*(steer) + 1.94;
			
			//  Compute error signals for each of the modules
			//      (command - current position)
			//  Adjust the error if either the command/feedback flips from 
			// 0 to 5 or vice versa (noted by bas(error) > 3.5)
			FrontLeftError = FrontLeftEq - FrontLeftAverage;
			if (FrontLeftError > 3.5){
				FrontLeftError = FrontLeftError - 5.0;
			}
			if (FrontLeftError < -3.5){
				FrontLeftError = FrontLeftError + 5.0;
			}
			
			FrontRightError = FrontRightEq - FrontRightAverage;
			if (FrontRightError > 3.5){
				FrontRightError = FrontRightError - 5.0;
			}
			if (FrontRightError < -3.5){
				FrontRightError = FrontRightError + 5.0;
			}

			BackLeftError = BackLeftEq - BackLeftAverage;
			if (BackLeftError > 3.5){
				BackLeftError = BackLeftError - 5.0;
			}
			if (BackLeftError < -3.5){
				BackLeftError = BackLeftError + 5.0;
			}

			BackRightError = BackRightEq - BackRightAverage;
			if (BackRightError > 3.5){
				BackRightError = BackRightError - 5.0;
			}
			if (BackRightError < -3.5){
				BackRightError = BackRightError + 5.0;
			}

			// Set steering motor commands to drive the error to zero
			FrontRightTurn.set(FrontRightError*1.5);
			FrontLeftTurn.set(FrontLeftError*1.5);
			BackRightTurn.set(BackRightError*1.5);
			BackLeftTurn.set(BackLeftError*1.5);
			
			// Set the power for the main drive motors
			FrontLeftDrive.set(Math.max (-1, Math.min(1, (dspeed + diffspeed))));
			BackLeftDrive.set(Math.max (-1, Math.min(1, (dspeed + diffspeed))));
			FrontRightDrive.set(Math.max (-1, Math.min(1, (dspeed - diffspeed))));
			BackRightDrive.set(Math.max (-1, Math.min(1, (dspeed - diffspeed))));
	
	}
}
