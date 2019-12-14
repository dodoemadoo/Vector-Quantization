package vector;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.awt.image.WritableRaster;
import java.io.*;
import java.util.ArrayList;

public class Form extends JFrame
{
    public JPanel panel1;
    private JLabel image1;
    private JButton button1;
    private JTextField rowTextField;
    private JTextField columnTextField;
    private JTextField level;
    private JButton decompressButton;
    static int height_image =0 ;
    static int width_image =0 ;
    static ArrayList<ArrayList<Integer>>arr =new ArrayList<ArrayList<Integer>>();
    public static void Write_Image(ArrayList<Integer>m,int width,int height){
        int [][]pixels=new int[height][width];
        for(int i=0,k=0;i<height;i++){
            for(int j=0;j<width;j++){
                if(k==m.size())
                {
                    break;
                }
                pixels[i][j]=m.get(k++);
            }
        }
        File fileout=new File("saved.jpg");
        BufferedImage image2=new BufferedImage(width,height,BufferedImage.TYPE_INT_RGB );

        for(int x=0;x<width ;x++)
        {
            for(int y=0;y<height;y++)
            {
                image2.setRGB(x,y,(pixels[y][x]<<16)|(pixels[y][x]<<8)|(pixels[y][x]));
            }
        }
        try
        {
            ImageIO.write(image2, "JPG", fileout);
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }
    public static void spilt(ArrayList<Integer>ar,int a,int b)
    {
        int index=0;
        int offset=a*b;
        for (int i=0;i<ar.size(); i++)
        {
            ArrayList<Integer>m=new ArrayList<>();
            while (index<offset)
            {
                if(i==ar.size())
                {
                    break;
                }
                m.add(ar.get(i));
                //i++;
                index++;
            }
            arr.add(m);         //ArrayList containing arraylists(pixels)
            index=0;
        }
    }
    public static ArrayList<Integer> Average(ArrayList<ArrayList<Integer>>k,int a ,int b )
    {
        ArrayList<Integer>out = new ArrayList<>();
        ArrayList<Integer>out1 = new ArrayList<>();
        int offest =a*b;
        for(int i=0;i<offest;i++)
        {
            int avg =0;
            for(int j=0;j<k.size();j++)
            {
                if(i>=k.get(j).size())
                {
                    break;
                }
                avg+=k.get(j).get(i);
            }
            out.add(avg);
        }
        int index =arr.get(arr.size()-1).size();
        int aa =arr.size();
        for (int i=0;i<out.size();i++)
        {
            if (i==index) {
                aa = arr.size() - 1;
            }
            out1.add(out.get(i)/aa);
        }
        return out1;
    }
    public static void compression(ArrayList<Integer>a ,int width ,int height,int levels)
    {
        ArrayList<Integer> compressed =new ArrayList<>();
        ArrayList<ArrayList<Integer>>decompressed =new ArrayList<>();
        ArrayList<Integer>Q =new ArrayList<>();
        for (int i=0;i<levels;i++)
            Q.add(i);
        spilt(a,width,height);
        boolean value =true;
        ArrayList<ArrayList<ArrayList<Integer>>>map= new ArrayList<>();
        map.add(arr);
        while(levels>1){
            ArrayList<ArrayList<Integer>>part=map.remove(0);
            ArrayList<Integer>avg = Average(part,width,height);
            ArrayList<ArrayList<Integer>>v1=new ArrayList<>(),v2=new ArrayList<>();
            for (int i=0;i<arr.size();i++)  //for every pixel
            {
                int x=0,y=0;
                for (int j=0;j<arr.get(i).size();j++)
                {
                    if (avg.size()>arr.get(i).size()&&value==true)
                    {
                        v1.add(arr.get(i));
                        value = false;
                        break;
                    }
                    if(avg.get(j)>arr.get(i).get(j))
                    {
                        x++;
                    }
                    else
                    {
                        y++;
                    }
                }
                if (x>y)
                {
                    v1.add(arr.get(i));
                }
                else
                {
                    v2.add(arr.get(i));
                }
            }
            map.add(v1);map.add(v2);    //nearest vectors for both left and right
            levels--;
        }
        ArrayList<ArrayList<Integer>>code_book=new ArrayList<>();
        for(int i=0;i<map.size();i++)
        {
            if(map.get(i).size()==0){
                code_book.add(Average(map.get(i), width, height)); ;}       // if a pixel is empty.
            else
            {
                code_book.add(Average(map.get(i), width, height));
            }
        }
        for (int i=0 ;i<arr.size();i++)
        {
            for (int j=0;j<map.size();j++)
            {
                for (int l=0;l<map.get(j).size();l++)
                {
                    if (arr.get(i) == map.get(j).get(l))        //no change condition
                    {
                            compressed.add(j);          //assigning decimal code for each vector in codebook
                            break;
                    }
                }
            }
        }
        System.out.println(compressed.size());
        System.out.println(arr.size());
        try {
            FileWriter fw = new FileWriter("compress.txt");
            PrintWriter pw = new PrintWriter(fw);
            for (int i=0;i<code_book.size();i++)
            {
                for (int j=0 ;j<code_book.get(i).size();j++)
                {
                    pw.print(code_book.get(i).get(j));
                    pw.print(' ');
                }
                pw.print('/');
                pw.println();
            }
            pw.print('#');
            pw.println();
            for (int i=0;i<compressed.size();i++)
            {
                pw.print(compressed.get(i));
                pw.print(' ');
            }
            pw.println();
        }
        catch (Exception e1)
        {
            System.out.println("Can't find file");
        }
    }
    public void Decompression()
    {
        ArrayList<ArrayList<Integer>>code_book=new ArrayList<>();
        ArrayList<Integer>compressed =new ArrayList<>();
        ArrayList<Integer>IMAGE =new ArrayList<>();
        String s="";
        try
        {
            FileReader fr =new FileReader("compress.txt");
            BufferedReader br = new BufferedReader(fr);
            String str =new String();
            while ((str=br.readLine())!=null)
            {
                s+=str;
            }
            br.close();
            String [] ss=s.split("#");
            String str1 ="" ;
            str1+=ss[0];
            String [] sss = str1.split("/");
            for (int i=0 ;i<sss.length;i++)
            {
                String [] str2 = sss[i].split(" ");
                ArrayList<Integer>aa =new ArrayList<>();
                for (int j=0 ; j<str2.length;j++)
                {
                    aa.add(Integer.parseInt(str2[j]));
                }
                code_book.add(aa);
            }
            String [] F =ss[1].split(" ");
            for (int i=0;i<F.length;i++)
            {
                compressed.add(Integer.parseInt(F[i]));
            }
            ArrayList<ArrayList<Integer>>decompressed=new ArrayList<>();
            for (int i=0;i<compressed.size();i++)
            {
                decompressed.add(code_book.get(compressed.get(i)));
            }
            System.out.println(decompressed.size());
            for (int i=0;i<decompressed.size();i++)
            {
                for (int j=0;j<decompressed.get(i).size();j++)
                {
                    IMAGE.add(decompressed.get(i).get(j));
                }
            }
            //System.out.println(IMAGE);
            Write_Image(IMAGE,width_image,height_image);
        }
        catch (Exception e1)
        {
            System.out.println("Can't find file");
        }
    }
    public Form()
    {

        add(panel1);
        setTitle("Vector Gui");
        setSize(400,500);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        button1.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try
                {
                    String row=rowTextField.getText();
                    String col =columnTextField.getText();
                    String lev =level.getText();
                    int r=Integer.parseInt(row);
                    int c=Integer.parseInt(col);
                    int num_of_levels =Integer.parseInt(lev);
                    ArrayList<Integer> pixel = new ArrayList<Integer>();
                    ArrayList<Integer> temp = new ArrayList<Integer>();
                    BufferedImage image = ImageIO.read(new File("i.jpg"));
                    int [][]matrix = new int[image.getWidth()][image.getHeight()];
                    WritableRaster wr = image.getRaster() ;
                    for (int y=0 ; y < image.getHeight() ; y++)
                        for (int x=0 ; x < image.getWidth() ; x++)
                            pixel.add(wr.getSample(x, y, 0));

                    width_image=image.getWidth();
                    height_image=image.getHeight();
                    compression(pixel,r,c,num_of_levels);

                }
                catch (Exception e1)
                {
                    JOptionPane.showMessageDialog(null, "Error when launch button");
                }

            }
        });
        decompressButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try
                {
                   Decompression();
                }
                catch (Exception e1)
                {
                    JOptionPane.showMessageDialog(null, "Error when launch button");
                }
            }
        });
    }

    private void createUIComponents() {
        // TODO: place custom component creation code here
        image1 =new JLabel(new ImageIcon("i.jpg"));
    }
}
