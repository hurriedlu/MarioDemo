package hurr.demo.sxt;

import java.awt.image.BufferedImage;

public class Mario implements Runnable{
    //用于存储当前的横纵坐标
    private int x;
    private int y;
    //用于表示当前的状态
    private String status;
    //用于显示当前状态对应的图像
    private BufferedImage show = null;
    //定义一个BackGround对象,用来获取当前场景的障碍物的信息
    private BackGround backGround = new BackGround();
    //用来实现马里奥的动作
    private Thread thread = null;//线程对象
    //马里奥的移动速度
    private int xSpeed;
    //马里奥的跳跃速度
    private int ySpeed;
    //定义一个索引
    private int index;

    //表示马里奥上升的时间
    private int upTime = 0;

    //用于判断马里奥是否走到了城堡的门口
    private boolean isOK;

    //用于判断马里奥是否死亡
    private boolean isDeath = false;

    //表示分数
    private int score = 0;

    public Mario() {
    }

    public Mario (int x,int y) {
        this.x = x;
        this.y = y;
        show = StaticValue.stand_R;//初始图像为向右站立
        this.status = "stand--right";
        thread = new Thread(this);//初始化线程
        thread.start();//启动线程
    }

    //马里奥的死亡方法
    public void death() {
        isDeath = true;
    }

    //马里奥向左移动
    public void leftMove() {
        //改变速度
        xSpeed = -5;

        //判断马里奥是否碰到旗子,如果碰到将无法移动
        if (backGround.isReach()) {
            xSpeed = 0;
        }

        //判断马里奥是否处于空中
        if (status.indexOf("jump") != -1) {
            status = "jump--left";//如果出于空中，则设定成为跳跃的状态
        }else {
            status = "move--left";//否则就是行走的状态
        }
    }

    //马里奥向右移动
    public void rightMove() {
        xSpeed = 5;

        //同理判断马里奥是否碰到旗子
        if (backGround.isReach()) {
            xSpeed = 0;
        }

        if (status.indexOf("jump") != -1) {
            status = "jump--right";//同理
        }else {
            status = "move--right";
        }
    }

    //马里奥向左停止
    public void leftStop() {
        xSpeed = 0;
        if (status.indexOf("jump") != -1) {
            status = "jump--left";
        }else {
            status = "stop--left";
        }
    }

    //马里奥向右停止
    public void rightStop() {
        xSpeed = 0;
        if (status.indexOf("jump") != -1) {
            status = "jump--right";
        }else {
            status = "stop--right";
        }
    }

    //马里奥跳跃
    public void jump() {
        if (status.indexOf("jump") == -1) {//如果不是跳跃状态
            if (status.indexOf("left") != -1) {//判断方向是否是向左的
                status = "jump--left";
            }else {
                status = "jump--right";
            }
            ySpeed = -10;//因为向上跳跃y值要减小
            upTime = 7;
        }

        //判断马里奥是否碰到旗子。注意此时y要置零，因为是跳跃
        if (backGround.isReach()) {
            ySpeed = 0;
        }

    }

    //马里奥下落
    public void fall() {//有可能是从障碍物上落下，因此不需要判断是否是跳跃状态
        if (status.indexOf("left") != -1) {
            status = "jump--left";
        }else {
            status = "jump--right";
        }
        ySpeed = 10;
    }

    @Override
    public void run() {
        while (true) {

            //判断是否处于障碍物上
            boolean onObstacle = false;

            //判断是否可以往右走
            boolean canRight = true;
            //判断是否可以往左走
            boolean canLeft = true;
            //判断马里奥是否到达旗杆位置
            if (backGround.isFlag() && this.x >= 500) {
                this.backGround.setReach(true);//表示到达旗杆的位置

                //判断旗子是否下落完成
                if (this.backGround.isBase()) {
                    status = "move--right";
                    if (x < 690) {
                        x += 5;
                    }else {
                        isOK = true;
                    }
                }else {
                    if (y < 395) {//判断是否在空中
                        xSpeed = 0;
                        this.y += 5;
                        status = "jump--right";//面向右边
                    }

                    if (y > 395) {//落到地上后停止下落
                        this.y = 395;
                        status = "stop--right";//并将状态设置为向右站立
                    }
                }

            }else {


                //遍历当前场景里所有的障碍物
                for (int i = 0; i < backGround.getObstacleList().size(); i++) {
                    Obstacle ob = backGround.getObstacleList().get(i);
                    //判断马里奥是否位于障碍物上
                    if (ob.getY() == this.y + 25 && (ob.getX() > this.x - 30 && ob.getX() < this.x + 25)) {
                        onObstacle = true;
                    }

                    //判断是否跳起来顶到砖块
                    if ((ob.getY() >= this.y - 30 && ob.getY() <= this.y - 20) && (ob.getX() > this.x - 30 && ob.getX() < this.x + 25)) {
                        if (ob.getType() == 0) {//如果是普通砖块就顶出去
                            backGround.getObstacleList().remove(ob);//将ob移除
                            score ++;//顶到砖块，分数++
                        }
                        upTime = 0;//使马里奥顶到砖块后立刻下落
                    }

                    //判断是否可以往右走
                    if (ob.getX() == this.x + 25 && (ob.getY() > this.y - 30 && ob.getY() < this.y + 25)) {
                        canRight = false;
                    }

                    //判断是否可以往左走
                    if (ob.getX() == this.x - 30 && (ob.getY() > this.y - 30 && ob.getY() < this.y + 25)) {
                        canLeft = false;
                    }

                }

                //判断马里奥是否碰到敌人死亡或者踩死蘑菇敌人
                for (int i = 0;i < backGround.getEnemyList().size();i++) {
                    Enemy e = backGround.getEnemyList().get(i);

                    //判断马里奥是否位于敌人上方
                    if (e.getY() == this.y + 20 && (e.getX() - 25 <= this.x && e.getX() + 35 >= this.x)) {
                        if (e.getType() == 1) {//只有蘑菇敌人才可以踩死
                            e.death();//敌人死亡
                            score += 2;//杀死一个敌人分数+2
                            upTime = 3;//使马里奥上升一小段
                            ySpeed = -10;
                        }else if (e.getType() == 2) {//否则就是食人花敌人，此时马里奥死亡
                            //马里奥死亡
                            death();
                        }
                    }

                    //判断马里奥是否碰到敌人，碰到的话死亡
                    if ((e.getX() + 35 > this.x && e.getX() - 25 < this.x) && (e.getY() + 35 > this.y && e.getY() - 20 < this.y)) {
                        //马里奥死亡
                        death();
                    }
                }

                //进行马里奥跳跃的操作
                if (onObstacle && upTime == 0) {
                    if (status.indexOf("left") != -1) {
                        if (xSpeed != 0) {
                            status = "move--left";
                        }
                        else {
                            status = "stop--left";
                        }
                    }
                    else {
                        if (xSpeed != 0) {
                            status = "move--right";
                        }
                        else {
                            status = "stop--right";
                        }
                    }
                }
                else {//如果不符合说明马里奥处于上升的状态
                    if (upTime != 0) {
                        upTime--;
                    }
                    else {//在没到达最高点前一直上升，然后掉落
                        fall();
                    }
                    y += ySpeed;
                }
            }
            if (canLeft && xSpeed < 0 || canRight && xSpeed > 0) {//判断是否在运动，可以走并且速度大于0
                x += xSpeed;
                //判断马里奥是否到了最左边
                if (x < 0) {
                    x = 0;
                }
            }
            //判断当前是否是移动状态
            if (status.contains("move")) {
                index = index == 0 ? 1 : 0;
            }
            //判断是否向左移动
            if ("move--left".equals(status)) {
                show = StaticValue.run_L.get(index);
            }
            //判断是否向右移动
            if ("move--right".equals(status)) {
                show = StaticValue.run_R.get(index);
            }
            //判断是否向左停止
            if ("stop--left".equals(status)) {
                show = StaticValue.stand_L;
            }

            //判断是否向右停止
            if ("stop--right".equals(status)) {
                show = StaticValue.stand_R;
            }

            //判断是否向左跳跃
            if ("jump--left".equals(status)) {
                show = StaticValue.jump_L;
            }
            //判断是否向右跳跃
            if ("jump--right".equals(status)) {
                show = StaticValue.jump_R;
            }

            try {//让线程休息
                Thread.sleep(50);
            }
            catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }

    public BufferedImage getShow() {
        return show;
    }

    public void setShow(BufferedImage show) {
        this.show = show;
    }

    public void setBackGround(BackGround backGround) {
        this.backGround = backGround;
    }

    public boolean isOK() {
        return isOK;
    }

    public boolean isDeath() {
        return isDeath;
    }

    public int getScore() {
        return score;
    }
}
