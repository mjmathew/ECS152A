public class DoublyLinkedList {

	public class Node {
		public Node next;
		public Node prev;
		public Event event;

		public Node(Event event) {
			this.next = null;
			this.prev = null;
			this.event = event;
		}
	}

	public int sizeOfList;
	public Node head;
	public Node tail;
	
	DoublyLinkedList() {
		this.head = null;
		this.tail = null;
		sizeOfList = 0;
	}

	/**
	 * @func insert: inserts node in the list
	 *
	 * @return void
	 */
	public void insert(Event event) {
		Node newNode = new Node(event);
		Node currNode = head;

		// list is empty 
		if (isEmpty()) {
			tail = newNode;
			head = newNode;
		}
		else {
			// add to front of list
			if (newNode.event.eventTime < head.event.eventTime) {
				head.prev = newNode;
				newNode.next = head;
				head = newNode;
			}
			else {
				// find the node to insert the new node after
				while(currNode.next != null && currNode.next.event.eventTime <= newNode.event.eventTime) {
					currNode = currNode.next;
				}

				// inserting node at the end of the list
				if(currNode.next == null) {
					tail = newNode;
					currNode.next = newNode;
					newNode.prev = currNode;
				}
				// inserting node in the middle of the list
				else {
					currNode.next.prev = newNode;
					newNode.next = currNode.next;
					newNode.prev = currNode;
					currNode.next = newNode;
				}
			}
		}
		sizeOfList++;
	}

	/**
	 * @func removeFirst: removes first node in the list
	 *
	 * @return returns event
	 */
	public Event removeFirst() {
		Node firstNodeTemp = head;

		if (head.next == null) {
			tail = null;
		}
		else {
			head.next.prev = null;
		}

		head = head.next;
		sizeOfList--;

		return firstNodeTemp.event;
	}

	/**
	 * @func isEmpty: checks if Doubly linked list is empty
	 *
	 * @return returns boolean
	 */
	public boolean isEmpty() {
		return this.head == null;
	}

	public void printitAll() {
		Node temp = head;

		while(temp != null) {
			System.out.println(temp.event.eventTime);
			temp = temp.next;
		}
	}
}

