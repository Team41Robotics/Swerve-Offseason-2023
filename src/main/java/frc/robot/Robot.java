package frc.robot;

import static frc.robot.RobotContainer.*;

import edu.wpi.first.wpilibj.TimedRobot;
import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.CommandScheduler;

public class Robot extends TimedRobot {
	private Command autonomousCommand;

	@Override
	public void robotInit() {
		robot = this;
		initSubsystems();
	}

	@Override
	public void robotPeriodic() {
		CommandScheduler.getInstance().run();
	}

	@Override
	public void disabledInit() {}

	@Override
	public void disabledPeriodic() {}

	@Override
	public void autonomousInit() {
		// autonomousCommand = robotContainer.getAutonomousCommand();

		// if (autonomousCommand != null) {
		// autonomousCommand.schedule();
		// }
	}

	@Override
	public void autonomousPeriodic() {}

	@Override
	public void teleopInit() {
		if (autonomousCommand != null) {
			autonomousCommand.cancel();
		}
	}

	@Override
	public void teleopPeriodic() {}

	@Override
	public void testInit() {
		CommandScheduler.getInstance().cancelAll();
	}

	@Override
	public void testPeriodic() {}

	public void configureButtons() {
		;
	}
}
