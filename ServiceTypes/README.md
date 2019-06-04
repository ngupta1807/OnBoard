Service:

UnBound Service:

1.Basically used for long repetitive task.

2.startService() is the method use to start unbound service.

3.stopService() is the method use to stop explicitly.

4.It is independent of the component from which it starts.


Bound Service:

1.Basically used for long repetitive task but bound with the component.

2.starts by bindService().

3.unbindService() is the method use to stop explicitly.

4.It is dependent of the component from which it starts

Intent Service:

1.Basically used for one time task. Whenever it completes the task, it destroys itself.

2.starts by startService().

3.stopself() calls implicitly to destroy.

4.It is independent of the component from which it starts