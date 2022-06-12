package hurr.demo.sxt;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.ArrayList;
import java.util.List;

//主框架
public class MyFrame extends JFrame implements KeyListener,Runnable {//继承JFrame实现GUI界面,实现KeyListener接口满足键盘监听的要求

    //存储所有的背景
    private List<BackGround> allBg = new ArrayList<>();
    //存储当前的背景
    private BackGround nowBg = new BackGround();
    //用于双缓存
    private Image offScreenImage = null;

    //定义马里奥对象
    private Mario mario = new Mario();

    //定义一个线程对象,用于实现马里奥的运动
    private Thread thread = new Thread(this);

    public MyFrame() {

        //设置窗口大小
        this.setSize(800, 600);
        //设置窗口居中显示
        this.setLocationRelativeTo(null);
        //设置窗口的可见性
        this.setVisible(true);
        //设置点击窗口上的关闭键,结束程序
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        //设置窗口大小不可变
        this.setResizable(false);
        //向窗口对象添加键盘监听器
        this.addKeyListener(this);
        //设置窗口名称
        this.setTitle("超级玛丽Demo By：蒋潮棨，2022030137");

        //调用一下常量类的初始化方法，初始化图片
        StaticValue.init();

        //初始化马里奥对象
        mario = new Mario(10,355);//初始化10,355。

        //创建全部场景
        for (int i = 1; i <= 3; i++) {
            allBg.add(new BackGround(i, i == 3 ? true : false));//关卡数，是否是第三关
        }

        //将第一个场景设置为当前场景
        nowBg = allBg.get(0);
        mario.setBackGround(nowBg);//将场景告诉马里奥
        //绘制图像
        repaint();

        thread.start();//启动线程
    }

    @Override
    public void paint(Graphics g) {
        if (offScreenImage == null) {//判断当前是否有图像
            offScreenImage = createImage(800,600);//如果没有就创建一下，注意尺寸需要与窗口保持一致
        }

        Graphics graphics = offScreenImage.getGraphics();

        graphics.fillRect(0,0,800,600);//填充图像

        //绘制背景
        graphics.drawImage(nowBg.getBgImage(),0,0,this);

        //绘制敌人。要先绘制食人花，因为食人花是藏在水管下面。因此需要先绘制，让障碍物覆盖住敌人
        for (Enemy e : nowBg.getEnemyList()) {
            graphics.drawImage(e.getShow(),e.getX(),e.getY(),this);
        }

        //绘制障碍物
        for (Obstacle ob : nowBg.getObstacleList()) {
            graphics.drawImage(ob.getShow(),ob.getX(),ob.getY(),this);
        }

        //绘制城堡
        graphics.drawImage(nowBg.getTower(),620,270,this);

        //绘制旗杆
        graphics.drawImage(nowBg.getGan(),500,220,this);

        //绘制马里奥
        graphics.drawImage(mario.getShow(),mario.getX(),mario.getY(),this);

        //添加分数
        Color c = graphics.getColor();
        graphics.setColor(Color.BLACK);
        graphics.setFont(new Font("黑体",Font.BOLD,25));
        graphics.drawString("当前的分数为: " + mario.getScore(),300,100);
        graphics.setColor(c);

        //将图像绘制到窗口中
        g.drawImage(offScreenImage,0,0,this);
    }

    public static void main(String[] args) {
        MyFrame myFrame = new MyFrame();
    }

    @Override
    public void keyTyped(KeyEvent e) {

    }

    //当键盘按下按键时调用，判断是否按下左右箭头
    @Override
    public void keyPressed(KeyEvent e) {
        //向右移动
        if (e.getKeyCode() == 39) {//右箭头
            mario.rightMove();
        }
        //向左移动
        if (e.getKeyCode() == 37) {//左箭头
            mario.leftMove();
        }
        //跳跃
        if (e.getKeyCode() == 38) {//如果上箭头被按下，就进行跳跃
            mario.jump();
        }
    }

    //当键盘松开按键时调用
    @Override
    public void keyReleased(KeyEvent e) {
        //向左停止
        if (e.getKeyCode() == 37) {
            mario.leftStop();
        }
        //向右停止
        if (e.getKeyCode() == 39) {
            mario.rightStop();
        }
    }

    @Override
    public void run() {
        while (true) {
            repaint();//重新绘制图像
            try {
                Thread.sleep(50);//休眠

                if (mario.getX() >= 775) {//如果横坐标大于775（800-25），就切换场景
                    nowBg = allBg.get(nowBg.getSort());//切换场景
                    //重置坐标，回到出生点的位置
                    mario.setBackGround(nowBg);
                    mario.setX(10);
                    mario.setY(395);
                }

                //判断马里奥是否死亡
                if (mario.isDeath()) {//如果获取到的状态是真，就证明马里奥已经死亡，可以结束程序
                    JOptionPane.showMessageDialog(this,"马里奥死亡!!!");
                    System.exit(0);
                }

                //判断游戏是否结束
                if (mario.isOK()) {
                    JOptionPane.showMessageDialog(this,"恭喜你!成功通关了");//弹出通关提示
                    System.exit(0);
                }

            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

}
