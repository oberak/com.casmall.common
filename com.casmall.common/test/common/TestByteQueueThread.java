package common;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import org.junit.Assert;
import org.junit.Test;

import com.casmall.common.ByteQueue;

public class TestByteQueueThread extends Object {
  private ByteQueue fifo;
  private byte[] srcData;

  @Test
  public void testAdd(){
	  fifo = new ByteQueue(20);
	  Assert.assertTrue(fifo.add("123".getBytes()));
  }
  public TestByteQueueThread() throws IOException {
    fifo = new ByteQueue(20);

    makeSrcData();
    System.out.println("srcData.length=" + srcData.length);

    Runnable srcRunnable = new Runnable() {
        public void run() {
          src();
        }
      };
    Thread srcThread = new Thread(srcRunnable);
    srcThread.start();

    Runnable dstRunnable = new Runnable() {
        public void run() {
          dst();
        }
      };
    Thread dstThread = new Thread(dstRunnable);
    dstThread.start();
  }

  private void makeSrcData() throws IOException {
    String[] list = {
        "The first string is right here",
        "The second string is a bit longer and also right here",
        "The third string",
        "ABCDEFGHIJKLMNOPQRSTUVWXYZ",
        "0123456789",
        "The last string in the list"
      };

    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    ObjectOutputStream oos = new ObjectOutputStream(baos);
    oos.writeObject(list);
    oos.flush();
    oos.close();
    
    srcData = baos.toByteArray();
  }

  private void src() {
    boolean justAddOne = true;
      int count = 0;

      while ( count < srcData.length ) {
        if ( !justAddOne ) {
          int writeSize = (int) ( 40.0 * Math.random() );
          writeSize = Math.min(writeSize, srcData.length - count);

          byte[] buf = new byte[writeSize];
          System.arraycopy(srcData, count, buf, 0, writeSize);
          fifo.add(buf);
          count += writeSize;

          System.out.println("just added " + writeSize + " bytes : "+new String(buf));
        } else {
          fifo.add(srcData[count]);
          count++;

          System.out.println("just added exactly 1 byte" + srcData[count]);
        }

        justAddOne = !justAddOne;
      }
  }

  private void dst() {
    try {
      boolean justAddOne = true;
      int count = 0;
      byte[] dstData = new byte[srcData.length];

      while ( count < dstData.length ) {
        if ( !justAddOne ) {
          byte[] buf = fifo.poll();
          if ( buf.length > 0 ) {
            System.arraycopy(buf, 0, dstData, count, buf.length);
            count += buf.length;
          }

          System.out.println(
            "just removed " + buf.length + " bytes");
        } else {
          byte b = fifo.remove();
          dstData[count] = b;
          count++;

          System.out.println(
            "just removed exactly 1 byte");
        }

        justAddOne = !justAddOne;
      }

      System.out.println("received all data, count=" + count);

      ObjectInputStream ois = new ObjectInputStream(
          new ByteArrayInputStream(dstData));

      String[] line = (String[]) ois.readObject();

      for ( int i = 0; i < line.length; i++ ) {
        System.out.println("line[" + i + "]=" + line[i]);
      }
    } catch ( ClassNotFoundException x1 ) {
      x1.printStackTrace();
    } catch ( IOException iox ) {
      iox.printStackTrace();
    } 
  }

  public static void main(String[] args) {

      new TestByteQueue();
    
//	  byte[] b = new byte[1];
//	  System.out.println(b[0]);
//    ByteQueue fifo = new ByteQueue(10);
//	fifo.add("1234567890".getBytes());
//	System.out.println(1);
//	fifo.add("1234567890".getBytes());
//	System.out.println(2);
//	fifo.add("1234567890".getBytes());
//	System.out.println(3);
//	fifo.add("1234567890".getBytes());
//	System.out.println(4);
  }
}



           
         
    
    