package hurr.demo.sxt;

import java.awt.image.BufferedImage;

public class Enemy implements Runnable {
    //存储当前坐标
    private int x,y;

    //存储敌人类型
    private int type;

    //判断敌人运动的方向
    private boolean face_to = true;

    //用于显示敌人的当前图像
    private BufferedImage show;

    //定义一个背景对象
    private BackGround bg;

    //食人花运动的极限范围，即最高值和最低值
    private int max_up = 0;
    private int max_down = 0;

    //定义线程对象，实现了敌人的运动
    private Thread thread = new Thread(this);
    //定义当前的图片的状态
    private int image_type = 0;

    //蘑菇敌人的构造函数
    public Enemy(int x,int y,boolean face_to,int type,BackGround bg) {
        this.x = x;
        this.y = y;
        this.face_to = face_to;
        this.type = type;
        this.bg = bg;
        show = StaticValue.mogu.get(0);
        thread.start();
    }
    //食人花敌人的构造函数
    public Enemy(int x,int y,boolean face_to,int type,int max_up,int max_down,BackGround bg) {
        this.x = x;
        this.y = y;
        this.face_to = face_to;
        this.type = type;
        this.max_up = max_up;
        this.max_down = max_down;
        this.bg = bg;
        show = StaticValue.flower.get(0);
        thread.start();
    }

    //死亡方法
    public void death() {
        show = StaticValue.mogu.get(2);//将蘑菇图片切换为死亡状态
        this.bg.getEnemyList().remove(this);
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public BufferedImage getShow() {
        return show;
    }

    public int getType() {
        return type;
    }

    @Override
    public void run() {
        while (true) {
            //判断是否是蘑菇敌人
            if (type == 1) {
                if (face_to) {//判断马里奥的朝向，为真表示蘑菇向左移动，否则向右移动
                    this.x -= 2;
                }else {
                    this.x += 2;
                }
                image_type = image_type == 1 ? 0 : 1;//切换图片状态

                show = StaticValue.mogu.get(image_type);
            }

            //定义两个布尔变量,判断敌人是否可以左右移动
            boolean canLeft = true;
            boolean canRight = true;

            for (int i = 0;i < bg.getObstacleList().size();i++) {
                Obstacle ob1 = bg.getObstacleList().get(i);
                //判断是否可以右走，和马里奥相同，符合条件说明有障碍物，就将对应的值置为false停止行走
                if (ob1.getX() == this.x + 36 && (ob1.getY() + 65 > this.y && ob1.getY() - 35 < this.y)) {
                    canRight = false;
                }

                //判断是否可以左走
                if (ob1.getX() == this.x - 36 && (ob1.getY() + 65 > this.y && ob1.getY() - 35 < this.y)) {
                    canLeft = false;
                }
            }

            //判断蘑菇敌人是否向左走，并碰到障碍物，或者走到最左侧
            if (face_to && !canLeft || this.x == 0) {
                face_to = false;//就让他向右走
            }
            else if ((!face_to) && (!canRight) || this.x == 764) {
                face_to = true;//同理，让他向左走
            }

            //判断是否是食人花敌人
            if (type == 2) {
                if (face_to) {//判断食人花的运动方向，
                    this.y -= 2;//向上走
                }else {
                    this.y += 2;//向下走
                }

                image_type = image_type == 1 ? 0 : 1;//切换食人花的图像

                //食人花是否到达极限位置
                if (face_to && (this.y == max_up)) {//判断是否是向上移动，并且到达极限
                    face_to = false;//改变运动方向
                }
                if ((!face_to) && (this.y == max_down)) {//同理 下极限判断
                    face_to = true;
                }

                show = StaticValue.flower.get(image_type);
            }

            try {
                Thread.sleep(50);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

        }
    }
}
