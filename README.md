# TelloFlightControl
Android demo app for Tello drone SDK commands.
Implements a UDP client and server with AsyncTask.
To simplify this demonstration app the MainActivity class exposes static variables for the UDP classes.
The AsyncTask classes are NOT static. This should be OK for simple text commands, entered by the user, from the main thread.
