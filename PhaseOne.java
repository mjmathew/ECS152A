import java.io.*;
import java.util.LinkedList;
import java.util.Queue;

public class PhaseOne {

    private static int maxbuffer;
    private static double serviceRate;
    private static double arrivalRate;
    private static double time = 0.0;
    private static int length = 0;          
    private static double meanQueueLength = 0.0;
    private static double utilization = 0.0;
    private static int droppedPackets = 0;


    public static void main(String[] args) {				

        // initialize the statistics variables
        double numOfPacketsInTransmitter = 0;
        double timeServerBusy = 0;

        /**
         * λ = arrivalRate = 0.1, 0.25, 0.4, 0.55, 0.65, 0.80, 0.90
         * mu = serviceRate = 1
         */
        serviceRate = Double.parseDouble(args[0]);     
        arrivalRate = Double.parseDouble(args[1]);    
        maxbuffer = Integer.parseInt(args[2]);

        DoublyLinkedList dblList = new DoublyLinkedList();
        Queue<Event> queue = new LinkedList<Event>();

        Event event = new Event();
        event.eventTime = time + negExpDistTime(arrivalRate);
        event.serviceTime = negExpDistTime(serviceRate);
        event.eventType = "arrival";
        dblList.insert(event);

        /**
         * 1. Get the first event from the GEL
         * 2. If the event is an arrival then process-arrival-event
         * 3. Otherwise, it must be a departure event and hence process-service-completion
         */
        for (int i = 0; i < 100000; i++) {

          if (dblList.isEmpty()) {
            System.err.println("GEL is empty");
            break;
          }

          Event gelEvent = dblList.removeFirst();
          time = gelEvent.eventTime;

          /**
           * PROCESS ARRIVAL
           * 
           * IF
           * 1. Create next arrival event
           * 2. If the transmitter has no packets, create departure event
           * 3. else if there are currently packets in buffer, add it to queue
           * 4. else the buffer has reached max capacity, drop packet
           */
          if (gelEvent.eventType == "arrival") {

            timeServerBusy = timeServerBusy + gelEvent.serviceTime;
            numOfPacketsInTransmitter = numOfPacketsInTransmitter + length;

            Event newArrivalEvent = new Event();
            newArrivalEvent.eventTime = time + negExpDistTime(arrivalRate);
            newArrivalEvent.serviceTime = negExpDistTime(serviceRate);
            newArrivalEvent.eventType = "arrival";
            dblList.insert(newArrivalEvent);

            if (length == 0) {
                Event newDepartureEvent = new Event();
                newDepartureEvent.eventTime = time + gelEvent.serviceTime;
                newDepartureEvent.eventType = "departure";
                dblList.insert(newDepartureEvent);
                length++;
            }
            else if (length - 1  < maxbuffer) {
                queue.add(gelEvent);
                length++;
            }
            else {
                gelEvent = null;
                droppedPackets++;
            }
          }

          /**
           * PROCESS Departure 
           *
           * ELSE
           * 1. Packet is departing so we remove from transmitter, and decrement length
           * 2. If buffer has no packets then we do nothing
           * 3. else buffer has packets so we dequeue first packet and create departure event
           */
          else {
            length--;

            if (length == 0) {
                continue;
            }
            else {
                Event eventFromQueue = queue.poll();
                Event newDepartureEvent = new Event();
                newDepartureEvent.eventTime = time + eventFromQueue.serviceTime;
                newDepartureEvent.eventType = "departure";
                dblList.insert(newDepartureEvent);
            }
          }
        }

        meanQueueLength = numOfPacketsInTransmitter/time;
        meanQueueLength = Math.round(meanQueueLength * 100);
        meanQueueLength = meanQueueLength / 100;

        utilization = timeServerBusy / time;
        utilization = Math.round(utilization * 100);
        utilization = utilization / 100;

        printStatisticsResults();
    }

    /**
     * @func printStatisticsResults: print out the results from the test/model
     *
     * @return void
     */
    public static void printStatisticsResults() {
        System.out.println("Phase 1 Results:");
        System.out.println("MAXBUFFER: " + maxbuffer);
        System.out.println("Arrival Rate: " + arrivalRate);
        System.out.println("Service Rate: " + serviceRate);
        System.out.println("Ending length of transmitter: " + length);
        System.out.println("Mean queue length : " + meanQueueLength);
        System.out.println("Utilization: " + utilization);
        System.out.println("Total number of packets dropped: " + droppedPackets);
    }

    /**
     * @func negExpDistTime: generates randomally negative exponential time
     *
     * @return double
     */
    public static double negExpDistTime(double rate) {
    	double u = Math.random();

    	return((-1/rate)*Math.log(1-u));
    }
}