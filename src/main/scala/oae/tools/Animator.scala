package oae.tools

import scala.collection.immutable.List

import javax.imageio._
import javax.swing._
import java.awt.event._

import java.awt._
import java.awt.image._

import oae._

import java.io._
import java.util.zip._

object Animator {

  var frames = List[(BufferedImage, Int)]((new BufferedImage(50,50,BufferedImage.TYPE_INT_RGB), 1))
  var currentFrame = 0
  var x = 150
  var y = 150
  var refreshing = false

  val fileChooser = new JFileChooser

  def go() {
    val frame = new JFrame("Orks and Explosions Animator")
    frame.setSize(800,600)
    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE)
    val bar = new JMenuBar
    frame.setJMenuBar(bar)

    val main = new JPanel
    main.setLayout(new BorderLayout)
    frame.getContentPane.add(main)
    val imagePanel = new JPanel{
      override def paint(g1:Graphics) {
        g1.setColor(Color.white)
        g1.fillRect(0,0,800,600)
        g1.drawImage(frames(currentFrame)._1, x, y, main)
      }
    }
    main.add(imagePanel, BorderLayout.CENTER)

    val controls = new JPanel
    main.add(controls, BorderLayout.NORTH)
    controls.add(new JLabel("Frame:"))
    val frameSelect = new JComboBox[Any]()
    controls.add(frameSelect)
    controls.add(new JLabel("Time:"))
    val frameTime = new JTextField(5)
    controls.add(frameTime)

    val previewButton = new JButton("Preview")
    controls.add(previewButton)
    previewButton.addActionListener(new ActionListener{
      override def actionPerformed(e:ActionEvent) {
//        val size = frames.head._1.size
//        val previewFrame = new JFrame(size, size);
//        preview

      }
    })

    val refresh = () => {
      refreshing = true
      frameSelect.removeAllItems
      (0 to (frames.size-1)).toList.foreach((i:Int) => {
        frameSelect.addItem(i)
      })
      frameSelect.setSelectedIndex(currentFrame)
      frameTime.setText(frames(currentFrame)._2+ "")
      frame.invalidate
      frame.validate
      frame.repaint()
      refreshing = false
    }

    frameTime.addKeyListener(new KeyAdapter {
      override def keyReleased(e:KeyEvent) {
        try {
          val time = Integer.parseInt(frameTime.getText)
          val image = frames(currentFrame)._1
          frames = frames.updated(currentFrame, (image, time))
        } catch {
          case _ => None
        }
      }
    })

    frameSelect.addItem("0")
    frameSelect.addActionListener(new ActionListener{
      override def actionPerformed(e:ActionEvent) {
        if(!refreshing) {
          currentFrame = frameSelect.getSelectedIndex
          refresh()
        }
      }
    })



    val newFile = () => {
      frames = List((new BufferedImage(50,50,BufferedImage.TYPE_INT_RGB), 1))
      currentFrame = 0
      refresh()
    }

    val openFile = () => {
      println("open File")

      if(fileChooser.showOpenDialog(frame) == JFileChooser.APPROVE_OPTION) {

        val zipIn = new ZipFile(fileChooser.getSelectedFile)
        var imageMap = Map[String, BufferedImage]()
        val enumeration = zipIn.entries
        while(enumeration.hasMoreElements) {
          val zipEntry = enumeration.nextElement
          val name = zipEntry.getName
          if(!name.equals("metadata.txt")) {
            val in = zipIn.getInputStream(zipEntry)
            imageMap = imageMap + (name -> ImageIO.read(in))
            in.close
          }
        }
        val metaDataEntry = zipIn.getEntry("metadata.txt")
        val metaDataInStream = zipIn.getInputStream(metaDataEntry)
        val reader = new BufferedReader(new InputStreamReader(metaDataInStream))
        var i = 0
        var line = reader.readLine
        frames = List[(BufferedImage, Int)]()
        while(line != null) {
          frames = frames ::: List((imageMap(i+".png"), Integer.parseInt(line)))
          i = i+1
          line = reader.readLine
        }
        currentFrame = 0
        reader.close
        zipIn.close
      }
      refresh()
    }

    val saveFile = () => {
      println("save File")
      if(fileChooser.showSaveDialog(frame) == JFileChooser.APPROVE_OPTION) {
        val zipOut = new ZipOutputStream(new FileOutputStream(fileChooser.getSelectedFile))

        val metaOut = new StringWriter()
        var i = 0
        frames.foreach((t:(BufferedImage, Int)) => {
          metaOut.append(t._2+"\n")
          zipOut.putNextEntry(new ZipEntry(i+".png"))
          i = i + 1
          ImageIO.write(t._1, "png", zipOut)
          zipOut.closeEntry
        })
        zipOut.putNextEntry(new ZipEntry("metadata.txt"))
        val metaBytes =  metaOut.toString.getBytes
        zipOut.write(metaBytes, 0, metaBytes.length)
        zipOut.close
      }
    }

    val exit = () => {
      System.exit(0)
    }

    val openImage = () => {
      if(fileChooser.showOpenDialog(frame) == JFileChooser.APPROVE_OPTION) {
        val image:BufferedImage = ImageIO.read(fileChooser.getSelectedFile)
        val time = frames(currentFrame)._2
        frames = frames.updated(currentFrame, (image, time))
      }
      refresh()
    }

    val addFrame = () => {
      frames = frames ::: List((new BufferedImage(50,50,BufferedImage.TYPE_INT_RGB), 1))
      currentFrame = frames.size - 1
      openImage()
    }

    val removeFrame = () => {
      frames = frames.filter(_ == frames(currentFrame))
      if(currentFrame > 0) {
        currentFrame -= 1
      } else {
        newFile
      }
      refresh()
    }

    val item = (title:String, action:()=>Unit) => {
      val i = new JMenuItem(title)
      i.addActionListener(new ActionListener {
        override def actionPerformed(e:ActionEvent) {
          action()
        }
      })
      i
    }
    val fileMenu = new JMenu("File")
    fileMenu.add(item("New", newFile))
    fileMenu.add(item("Open", openFile))
    fileMenu.add(item("Save", saveFile))
    fileMenu.add(item("Exit", exit))
    bar.add(fileMenu)

    val frameMenu = new JMenu("Animation")
    frameMenu.add(item("Select Image", openImage))
    frameMenu.add(item("Add Frame", addFrame))
    frameMenu.add(item("Remove Frame", removeFrame))
    bar.add(frameMenu)

    frame.setVisible(true)
  }
}