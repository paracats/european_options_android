package com.bs;

public class EuropeanUpInPut extends EuropeanBarrierOption implements
		EuropeanOption {

	public EuropeanUpInPut(double Spot_, double Strike_, double r_, double d_,
			double T_, double vol_, double Barrier_, double Rebate_) {
		super(Spot_, Strike_, r_, d_, T_, vol_, Barrier_, Rebate_);
		//the option is meaningless if Spot>Barrier => convert to a vanilla call??
		if(Spot>=Barrier){
			//EXCEPTION
			throw new IllegalArgumentException("The price has reached the barrier level! "
												+ "Spot should be less than Barrier.");
		}
	}

	@Override
	public double getPrice() {
		if(Strike>Barrier)
			return A(-1)-B(-1)+D(-1,-1)+E(-1);
		else
			return C(-1,-1) + E(-1);
	}

	@Override
	public double getDelta() {
		if(Strike>Barrier){
			return DerA(1,EuropeanBarrierParams.SPOT,-1)
					-DerB(1,EuropeanBarrierParams.SPOT,-1)
					+DerD(1,EuropeanBarrierParams.SPOT,-1,-1)
					+DerE(1,EuropeanBarrierParams.SPOT,-1);
		}else{
			return DerC(1,EuropeanBarrierParams.SPOT,-1,-1) 
					+ DerE(1,EuropeanBarrierParams.SPOT,-1);}
	}

	@Override
	public double getGamma() {
		if(Strike>Barrier){
			return DerA(2,EuropeanBarrierParams.SPOT,-1)
					-DerB(2,EuropeanBarrierParams.SPOT,-1)
					+DerD(2,EuropeanBarrierParams.SPOT,-1,-1)
					+DerE(2,EuropeanBarrierParams.SPOT,-1);
		}else{
			return DerC(2,EuropeanBarrierParams.SPOT,-1,-1) 
					+ DerE(2,EuropeanBarrierParams.SPOT,-1);}
	}

	@Override
	public double getTheta() {
		if(Strike>Barrier){
			return DerA(1,EuropeanBarrierParams.EXPIRY,-1)
					-DerB(1,EuropeanBarrierParams.EXPIRY,-1)
					+DerD(1,EuropeanBarrierParams.EXPIRY,-1,-1)
					+DerE(1,EuropeanBarrierParams.EXPIRY,-1);
		}else{
			return DerC(1,EuropeanBarrierParams.EXPIRY,-1,-1) 
					+ DerE(1,EuropeanBarrierParams.EXPIRY,-1);}
	}

	@Override
	public double getSpeed() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public double getVega() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public double getRho() {
		// TODO Auto-generated method stub
		return 0;
	}
	public static double[] UpInDefault = {100.,110.,0.08,0.04,0.25,0.5,105.,3.};	

}