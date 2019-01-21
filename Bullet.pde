class Asteroid {
  float Px = random(width);
  float Py = random(height);

  int Victim = 0;
  boolean vol = false;
  Asteroid() {
    if(random(1) > 0.5) {
      vol = true;
    }
  }
  void r() {
      Px = random(width);
      Py = random(height);
  }
  void move(){
    //float[] answer = closestPlayer();
    //while(closestPlayer() != null) {
    //  answer = closestPlayer();
    //}
    //if(answer[0] >= Px) {
    //  Px+=3;
    //}else{
    //  Px-=3;
    //}
    //if(answer[1] >= Py) {
    //  Py+=3;
    //}else{
    //  Py-=3;
    //}
    
    if(Px >= width) {
      Px = -9;
      Py += random(10);
    }
    if(Px <= -10) {
      Px = width - 1;
      Py += random(10);
    }
    if(Py >= height) {
      Py = -9;
      Px += random(10);
    }
    if(Py <= -10) {
      Py = height - 1;
      Px += random(10);
    }
    if(vol){
    Py-=4;
    Px-=4;
    } else {Py+=4; Px-=4;}
    
    
  }
  
  float distance(float Tx, float Ty, float Rx, float Ry) {
    return (float)Math.hypot(Tx - Rx, Ty - Ry);
  }
  
  float[] closestPlayer() {
    float X = -10000;
    float Y = -10000;
    float posX = -10000;
    float posY = -10000;
    //Fighter closestPlayer = null;
    for (int i = pop.pop.size() - 1; i >= 0; i--) {
      Fighter player = pop.pop.get(i);
      if (distance(Px, Py, player.posX, player.posY) < distance(Px, Py, posX, posY)) {
        posX = player.posX;
        posY = player.posY;
        X = player.posX;
        Y = player.posY;
      }
    }
    float[] answer = new float[2];
    answer[0] = X;
    answer[1] = Y;
    return answer; 
  }
  
  void draw() {
    fill(255, 0, 0);
    stroke(255, 0, 0);
    ellipse(Px, Py, 5, 5);
    move();
  }
}
