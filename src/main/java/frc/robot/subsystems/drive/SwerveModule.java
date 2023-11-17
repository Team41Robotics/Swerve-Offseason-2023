package frc.robot.subsystems.drive;

import static frc.robot.constants.Constants.*;
import static java.lang.Math.*;

import org.littletonrobotics.junction.Logger;

import com.ctre.phoenix6.hardware.CANcoder;
import com.revrobotics.CANSparkMax;
import com.revrobotics.CANSparkMaxLowLevel.MotorType;
import edu.wpi.first.math.MathUtil;
import edu.wpi.first.math.controller.PIDController;
import edu.wpi.first.math.geometry.Rotation2d;
import edu.wpi.first.math.kinematics.SwerveModulePosition;
import edu.wpi.first.math.kinematics.SwerveModuleState;

public class SwerveModule {
	public PIDController pidTurn = new PIDController(9, 0, 0);
	public PIDController pidSpeed = new PIDController(0, 0, 0);

	// public static final State zeroState = new State(0, 0);
	// public TrapezoidProfile profile = new TrapezoidProfile(SWERVE_TURN_TRAPEZOID, zeroState, zeroState);
	// public double profile_t0 = Timer.getFPGATimestamp();
	private ModuleIO io;
	private final ModuleIOInputsAutoLogged inputs = new ModuleIOInputsAutoLogged();
	private int index;
	CANcoder encoder;
	CANSparkMax turn_motor, drive_motor;
	double offset;
	SwerveModuleState target_state = new SwerveModuleState();
	public SwerveModule(ModuleIO io, int index){
		this.io = io;
		this.index = index;
	}
	public SwerveModule(int port_cancoder, int port_turn_motor, int port_drive_motor, double offset) {
		encoder = new CANcoder(port_cancoder, "rio");
		turn_motor = new CANSparkMax(port_turn_motor, MotorType.kBrushless);
		drive_motor = new CANSparkMax(port_drive_motor, MotorType.kBrushless);
		this.offset = offset;
		pidTurn.enableContinuousInput(-PI, PI);
	}

	public double getDirection() {
		return inputs.turnAbsolutePosition.getRadians();
		//return (encoder.getAbsolutePosition().getValue() - this.offset) * 2 * PI;
	}



	public double getVelocity() { // in m/s
		return inputs.driveVelocityRadPerSec * SWERVE_WHEEL_RAD;
		//return drive_motor.getEncoder().getVelocity() * 2 * PI / 60 * L2_DRIVE_RATIO * SWERVE_WHEEL_RAD;
	}

	public double getDrivePosition() {
		return inputs.drivePositionRad * SWERVE_WHEEL_RAD;
		//return drive_motor.getEncoder().getPosition() * 2 * PI * L2_DRIVE_RATIO * SWERVE_WHEEL_RAD;
	}

	public void setState(SwerveModuleState state) {
		state = SwerveModuleState.optimize(state, new Rotation2d(getDirection()));
		target_state = state;
		// double delta = getDirection() - state.angle.getRadians();
		// delta = MathUtil.angleModulus(delta);
		// profile = new TrapezoidProfile(SWERVE_TURN_TRAPEZOID, new State(delta, getAngularVelocity()), zeroState);
		// profile_t0 = Timer.getFPGATimestamp();
	}

	public SwerveModulePosition getPosition() {
		return new SwerveModulePosition(getDrivePosition(), new Rotation2d(getDirection() + PI / 2));
	}

	double drive_v, turn_v;

	public void fixOffset() {
		System.out.println("Offset for Cancoder: " + this.encoder.getDeviceID() + " is: " + getDirection() / 2 / PI);
	}

	public void periodic() {
		io.updateInputs(inputs);
		Logger.processInputs("Drive/Module" + Integer.toString(index), inputs);

		double MAX_SPEED = 1;

		if(io != null){
			io.setDriveVoltage(Math.abs(Math.cos((getDirection() - target_state.angle.getRadians())))
				* target_state.speedMetersPerSecond
				/ MAX_SPEED
				* 9);
			io.setTurnVoltage(MathUtil.angleModulus(getDirection() - target_state.angle.getRadians()) * 2);
		}else{

		drive_motor.setVoltage(Math.abs(Math.cos((getDirection() - target_state.angle.getRadians())))
				* target_state.speedMetersPerSecond
				/ MAX_SPEED
				* 9);
		turn_motor.setVoltage(MathUtil.angleModulus(getDirection() - target_state.angle.getRadians()) * 2);
		}
		// turn_motor.setVoltage(pidTurn.calculate(getDirection(), target_state.angle.getRadians()));
		// turn_motor.setVoltage(pidTurn.calculate(target_state.angle.getRadians(), getDirection()));
		// System.out.println(target_state.speedMetersPerSecond / MAX_SPEED * 9);
		// System.out.println((MathUtil.angleModulus(getDirection() - target_state.angle.getRadians()) * 3));
	}
}