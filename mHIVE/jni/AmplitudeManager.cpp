class AmplitudeManager 
{
	private:
		float attack, decay, sustain, release;
	
	public:

		AmplitudeManager()
		{
		/*
			struct sigevent sevp;
			sevp.sigev_notify=SIGEV_THREAD;
			sevp.sigev_value.sival_ptr=&gTimer_id;
			sevp.sigev_notify_function=&ADSRCallback;
			sevp.sigev_notify_attributes=NULL;
			int timer_result = timer_create(CLOCK_REALTIME, &sevp, &gTimer_id);
			if(timer_result != 0)
			{
				__android_log_print(ANDROID_LOG_ERROR, "fmod", "Timer Failure");
			}
			//start time
			struct itimerspec spec;
			spec.it_interval.tv_sec = 0; //seconds
			spec.it_interval.tv_nsec = 10000000; //nanoseconds
			spec.it_value.tv_sec = 1; //seconds
			spec.it_value.tv_nsec = 0; //nanoseconds
			if(0 != timer_settime(gTimer_id, 0, &spec, NULL))
			{
				__android_log_print(ANDROID_LOG_ERROR, "fmod", "Timer Set Failure");
			}*/
		}


};
