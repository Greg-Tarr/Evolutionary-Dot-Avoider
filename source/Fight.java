import processing.core.*; 
import processing.data.*; 
import processing.event.*; 
import processing.opengl.*; 

import java.util.HashMap; 
import java.util.ArrayList; 
import java.io.File; 
import java.io.BufferedReader; 
import java.io.PrintWriter; 
import java.io.InputStream; 
import java.io.OutputStream; 
import java.io.IOException; 

public class Fight extends PApplet {

//Globals
Population pop;
Fighter genPlayerTemp;
boolean showNothing = false;
int upToGen = 0;
int nextConnectionNo = 1000;
//Noise
int frameCheck = 0;
//Config
int framerate = 600;
int borderThickness = 0;


int Dots = 30;
int Asteroids = 60;
ArrayList<Dot> obstacles = new ArrayList<Dot>();
ArrayList<Asteroid> bullets = new ArrayList<Asteroid>();
public void setup() {
  
  //fullScreen();
  frameRate(framerate);
  int PopulationTop = 20;
  for(int i = 0; i < Asteroids; i++) {
    bullets.add(new Asteroid());
  }
  for (int i = Dots; i >= 0; i--) {
    obstacles.add(new Dot(0, 255, 255));
  }
  //Set Values and et al.
  pop = new Population(PopulationTop);
}

public void draw() {
    
    
    if(!showNothing) {
    background(0);
    for(int i = bullets.size() - 1; i >= 0; i--) {
      Asteroid a = bullets.get(i);
      a.draw();
    }
    for (int i = obstacles.size() - 1; i >= 0; i--) {
      //obstacles.get(i).draw();
    }
    
    } else {
    for(int i = bullets.size() - 1; i >= 0; i--) {
      Asteroid a = bullets.get(i);
      a.move();
    }
    

    
    }
    if (!pop.done()) {//if any players are alive then update them
      pop.updateAlive();
    } else {
      frameCount = 0;
      pop.naturalSelection();
      for(int i = bullets.size() - 1; i >= 0; i--) {
        Asteroid a = bullets.get(i);
        a.r();
      }
      //obstacles = new ArrayList<Dot>();
      for (int i = Dots; i >= 0; i--) {
        obstacles.get(i).r();
      }
    }
    if(!showNothing) {
      drawFighters();
    }

  
}

public void drawFighters() {
  for (int i = pop.pop.size() - 1; i >= 0; i--) {
    Fighter fighter = pop.pop.get(i);
    stroke(0, 255, 255);
    fill(0, 255, 255);  
    strokeWeight(1);
    if(!fighter.dead)  {
      ellipse(fighter.posX, fighter.posY, fighter.posWidth, fighter.posWidth);
    } else {
      fighter.posX = -1000;
      fighter.posY = -1000;
    }
  }
}

public void drawBrain() {  //show the brain of whatever genome is currently showing
  if (!showNothing) {
    fill(255);
    textAlign(LEFT);
    textSize(width/50);
    text("Leader's Score: " + pop.pop.get(0).score, 30, height - 30);
    textAlign(RIGHT);
    text("Frame #: " + frameCount, width, height - 30);
    //Genome Size config
    //Genome drawing config
    for(int i = 0; i < pop.pop.size() - 1; i+=1) {
      //pop.pop.get((int)random(0, pop.pop.size() - 1)).brain.drawGenome((i*250) + 10, 10, 240, 100, 10);
      pop.pop.get(0).brain.drawGenome((i*250) + 10, 10, 240, 100, 10);
    }

  }
}
class Asteroid {
  float Px = random(width);
  float Py = random(height);

  int Victim = 0;
  boolean vol = false;
  Asteroid() {
    if(random(1) > 0.5f) {
      vol = true;
    }
  }
  public void r() {
      Px = random(width);
      Py = random(height);
  }
  public void move(){
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
  
  public float distance(float Tx, float Ty, float Rx, float Ry) {
    return (float)Math.hypot(Tx - Rx, Ty - Ry);
  }
  
  public float[] closestPlayer() {
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
  
  public void draw() {
    fill(255, 0, 0);
    stroke(255, 0, 0);
    ellipse(Px, Py, 5, 5);
    move();
  }
}
class Dot {
  float w = 2;
  float posX = random(width);
  float posY = random(height);
  float Dcolor = 255;
  float Dcolor1 = 255;
  float Dcolor2 = 255;
  Dot(float Tcolor, float Tcolor1, float Tcolor2){
    Dcolor = Tcolor;
    Dcolor1 = Tcolor1;
    Dcolor2 = Tcolor2;
  }
  public void draw() {
      stroke(255);
      fill(Dcolor, Dcolor1, Dcolor2);
      ellipse(posX, posY, w, w);
  }
  public void kill(){
    w = 0;
  }
  public void r() {
    w = 2;
  }
}
class Fighter {
  float unadjustedFitness;
  float fitness;
  Genome brain;
  int bestScore = 0;
  boolean dead;
  int score;
  int gen = 0;
  int genomeInputs = 4;
  int genomeOutputs = 4;
  int dotsCollected = 0;
  int myNum = (int)random(50, 100);
  float lasted = 0;
  float FighterSpeed = 5;
  long UID = (long)random(4234424);
  float[] vision = new float[genomeInputs];//the input array fed into the neuralNet 
  float[] decision = new float[genomeOutputs]; //the out put of the NN 
  //-------------------------------------
  int hunger =  400;
  float posX = random(width);//random(width);width/2;//
  float posY = random(height);//random(height);height/2;//
  
  float posWidth = 10;
  
  
  Fighter() {
    UID = (long)random(10000000);
    frameCount = 0;
    lasted = 0;
    brain = new Genome(genomeInputs, genomeOutputs);
  }
  
  public void calculateFitness() {
    fitness = lasted / 1000;
  }
  
  //----------------------------------------------------------------------------------------------------------------------------------------------------------
  
  
  public void moveUp(float amount) {
    float newY = posY + amount;
    if (newY > borderThickness  && newY < height - borderThickness) {
      posY = newY;
    } else {
      if(posY >= height + 9) {
        posY = -8;
      }
      if(posY <= -9) {
        posY = height + 8;
      }
    }
  }
  public void moveDown(float amount) {
    float newY = posY - amount;
    if (newY > borderThickness  && newY < height - borderThickness) {
      posY = newY;
    } else {
      if(posY >= height + 9) {
        posY = -8;
      }
      if(posY <= -9) {
        posY = height + 8;
      }
    }
  }
  
  public void collectDots() {
   for(int i = obstacles.size() - 1; i >= 1; i--) {
     Dot dot = obstacles.get(i);
     if (distance(dot.posX, dot.posY, this.posX, this.posY) < (posWidth)) {
       dotsCollected += 1;
       hunger += 100;
       dot.posX = random(width);
       dot.posX = random(height);
       break;
     }
    }

  }
  
  
  public void moveLeft(float amount) {
    float newX = posX - amount;
    if (newX > 0  && newX < width) {
      posX = newX;
    } else {
      if(posX >= width) {
        posX = 0;
      }
      if(posX <= -9) {
        posX = width;
      }
    }
  }
  public void moveRight(float amount) {
    float newX = posX + amount;
    if (newX > 0 && newX < width) {
      posX = newX;
    } else {
      if(posX >= width + 9) {
        posX = -8;
      }
      if(posX <= -9) {
        posX = width + 8;
      }
    }
  }  

  public float distance(float Tx, float Ty, float Rx, float Ry) {
    return (float)Math.hypot(Tx - Rx, Ty - Ry);
  }
  
  public float[] closestDot() {
    float X = 0;
    float Y = 0;
    Asteroid closestDot = bullets.get(bullets.size() - 1);
    for (int i = bullets.size() - 1; i >= 1; i--) {
      Asteroid obstacle = bullets.get(i);
      if (distance(posX, posY, obstacle.Px, obstacle.Py) <= distance(posX, posY, closestDot.Px, closestDot.Py)) {
        closestDot = obstacle;
        X = closestDot.Px;
        Y = closestDot.Py;
      }
    }
    float[] answer = new float[2];
    answer[0] = X;
    answer[1] = Y;
    return answer; 
  }
  
  
  public void populateInputs() {
    //vision[0] = -10;
    vision[0] = 500/(posX*width);
    vision[1] = 500/(posY*height);
    float[] answer = closestDot();
    vision[2] = 500/((answer[1] - posY)*height);
    //vision[4] = answer[1];
    vision[3] = 500/((answer[0] - posX)*width);
    //float[] answers = closestDotEat();
    //vision[4] = 500/((answers[1] - posY)*height);
    //vision[4] = answer[1];
    //vision[5] = 500/((answers[0] - posX)*width);
    //OP ANSWERS
    //if(answer[0] >= posX) {
    //  vision[3]= -100;
    //}else{
    //  vision[3]= 100;;
    //}
    //if(answer[1] >= posY) {
    //  vision[4]= -100;
    //}else{
    //  vision[4]= 100;
    //}

  }
  public float[] closestDotEat() {
    float X = 0;
    float Y = 0;
    Dot closestDot = obstacles.get(obstacles.size() - 1);
    for (int i = obstacles.size() - 1; i >= 1; i--) {
      Dot obstacle = obstacles.get(i);
      if (distance(posX, posY, obstacle.posX, obstacle.posY) <= distance(posX, posY, closestDot.posX, closestDot.posY)) {
        closestDot = obstacle;
        X = closestDot.posX;
        Y = closestDot.posY;
      }
    }
    float[] answer = new float[2];
    answer[0] = X;
    answer[1] = Y;
    return answer; 
  }
  public void useOutputs() {
    decision = brain.feedForward(vision);
    moveUp(decision[0] * posWidth * 2);
    moveDown(decision[1] * posWidth * 2);
    moveLeft(decision[2] * posWidth * 2);
    moveRight(decision[3] * posWidth * 2);
    
    //Fighter closestEnemy = getClosestEnemy();
    //posD = (float)Math.atan((posY-closestEnemy.posY)/(posX-closestEnemy.posX));
  }
  
  public void update() {
    hunger -= 1;
    collectDots();
    lasted = frameCount;

    for(int i = bullets.size() - 1; i >= 0; i--) {
      Asteroid a = bullets.get(i);
        if(distance(posX, posY, a.Px, a.Py) < posWidth) {
          dead = true;
        }
    }
    if(frameCount > 5000 || pop.done()) {
      dead =  true;
      hunger = 200;
      dotsCollected = 1;
    }

  }
  
    
  //---------------------------------------------------------------------------------------------------------------------------------------------------------  
  public Fighter clone() {
    Fighter clone = new Fighter();
    clone.brain = brain.clone();
    clone.fitness = fitness;
    clone.brain.generateNetwork(); 
    clone.gen = gen;
    clone.bestScore = score;
    return clone;
  }
  public Fighter crossover(Fighter parent2) {
    Fighter child = new Fighter();
    child.brain = brain.crossover(parent2.brain);
    child.brain.generateNetwork();
    return child;
  }
}
  //--------------------------------------------------------------------------------------------------------------------------------------------------------
int[] keys = new int[100];
int[] Values = new int[2];
int num = 0;


public void mousePressed() {
  showNothing = !showNothing;
}
public void keyPressed() {
  if(key == 'n') {
    showNothing = !showNothing;
  }
}
public void keyReleased() {
}
//1001
class Population {
  ArrayList<Fighter> pop = new ArrayList<Fighter>();
  Fighter bestFighter;//the best ever player 
  int bestScore =0;//the score of the best ever player
  int gen;
  ArrayList<connectionHistory> innovationHistory = new ArrayList<connectionHistory>();
  ArrayList<Fighter> genFighters = new ArrayList<Fighter>();
  ArrayList<Species> species = new ArrayList<Species>();

  boolean massExtinctionEvent = true;
  ArrayList<Fighter> populationLife = new ArrayList<Fighter>();




  //------------------------------------------------------------------------------------------------------------------------------------------
  //constructor
  Population(int size) {

    for (int i =0; i<size; i++) {
      pop.add(new Fighter());
      pop.get(i).brain.generateNetwork();
      pop.get(i).brain.mutate(innovationHistory);
    }
  }
    //------------------------------------------------------------------------------------------------------------------------------------------
  //update all the players which are alive
  public void updateAlive() {
    for (int i = 0; i< pop.size(); i++) {
      if (!pop.get(i).dead) {
        //TODO POPULATION LIFE
        pop.get(i).populateInputs();//get inputs for brain 
        pop.get(i).useOutputs();//use outputs from neural network
        pop.get(i).update();//move the player according to the outputs from the neural network
        
        
      }
    }
  }  //------------------------------------------------------------------------------------------------------------------------------------------
  //returns true if all the players are dead      sad
  public boolean done() {
    for (int i = 0; i < pop.size(); i++) {
      if (!pop.get(i).dead) {
        return false;
      }
    }
    return true;
  }
  //------------------------------------------------------------------------------------------------------------------------------------------
  //sets the best player globally and for this gen
  public void setBestFighter() {
    
    Fighter tempBest = pop.get(0);
    
    tempBest.gen = gen;
    println("Best for this Gen:", tempBest.score);
    
  }

  //------------------------------------------------------------------------------------------------------------------------------------------------
  //this function is called when all the players in the population are dead and a new generation needs to be made
  public void naturalSelection() {
    speciate();//seperate the population into species 
    calculateFitness();//calculate the fitness of each player
    sortSpecies();//sort the species to be ranked in fitness order, best first
    if (massExtinctionEvent) { 
      massExtinctionEvent = false;
    }
    cullSpecies();//kill off the bottom half of each species
    setBestFighter();//save the best player of this gen
    killStaleSpecies();//remove species which haven't improved in the last 15(ish) generations
    killBadSpecies();//kill species which are so bad that they cant reproduce


    println("------ Generation #:", gen, "Number of Mutations Total:", innovationHistory.size(), "Number of Species: " + species.size(), "------");

  
    float averageSum = getAvgFitnessSum();
    ArrayList<Fighter> children = new ArrayList<Fighter>();//the next generation
    println("Species:");               
    for (int j = 0; j < species.size() - 1; j++) {//for each species

      println("Best Unadjusted Fitness for Species:", j + 1, ":", species.get(j).bestFitness);
      //children.add(species.get(j).champ.clone());//add champion without any mutation
      
      int NoOfChildren = floor(species.get(j).averageFitness/averageSum * pop.size()) -1;//the number of children this species is allowed, note -1 is because the champ is already added
      for (int i = 0; i< NoOfChildren; i++) {//get the calculated amount of children from this species
        children.add(species.get(j).giveMeBaby(innovationHistory));
      }
    }

    while (children.size() < pop.size()) {//if not enough babies (due to flooring the number of children to get a whole int) 
      children.add(species.get(0).giveMeBaby(innovationHistory));//get babies from the best species
    }
    pop.clear();
    pop = (ArrayList)children.clone(); //set the children as the current population
    gen+=1;
    for (int i = 0; i< pop.size(); i++) {//generate networks for each of the children
      pop.get(i).brain.generateNetwork();
    }

  }

  //------------------------------------------------------------------------------------------------------------------------------------------
  //seperate population into species based on how similar they are to the leaders of each species in the previous gen
  public void speciate() {
    for (Species s : species) {//empty species
      s.players.clear();
    }
    for (int i = 0; i< pop.size(); i++) {//for each player
      boolean speciesFound = false;
      for (Species s : species) {//for each species
        if (s.sameSpecies(pop.get(i).brain)) {//if the player is similar enough to be considered in the same species
          s.addToSpecies(pop.get(i));//add it to the species
          speciesFound = true;
          break;
        }
      }
      if (!speciesFound) {//if no species was similar enough then add a new species with this as its champion
        species.add(new Species(pop.get(i)));
      }
    }
  }
  
  //------------------------------------------------------------------------------------------------------------------------------------------
  //calculates the fitness of all of the players 
  public void calculateFitness() {
    for (int i =1; i<pop.size(); i++) {
      pop.get(i).calculateFitness();
    }
  }
  
  //------------------------------------------------------------------------------------------------------------------------------------------
  //sorts the players within a species and the species by their fitnesses
  public void sortSpecies() {
    //sort the players within a species
    for (Species s : species) {
      s.sortSpecies();
    }

    //sort the species by the fitness of its best player
    //using selection sort like a loser
    ArrayList<Species> temp = new ArrayList<Species>();
    for (int i = 0; i < species.size(); i ++) {
      float max = 0;
      int maxIndex = 0;
      for (int j = 0; j< species.size(); j++) {
        if (species.get(j).bestFitness > max) {
          max = species.get(j).bestFitness;
          maxIndex = j;
        }
      }
      temp.add(species.get(maxIndex));
      species.remove(maxIndex);
      i--;
    }
    species = (ArrayList)temp.clone();
  }

  //------------------------------------------------------------------------------------------------------------------------------------------
  //kills all species which haven't improved in 15 generations
  public void killStaleSpecies() {
    for (int i = 2; i< species.size(); i++) {
      if (species.get(i).staleness >= 10) {
        species.remove(i);
        i--;
      }
    }
  }

  //------------------------------------------------------------------------------------------------------------------------------------------
  //if a species sucks so much that it wont even be allocated 1 child for the next generation then kill it now
  public void killBadSpecies() {
    float averageSum = getAvgFitnessSum();

    for (int i = 1; i< species.size(); i++) {
      if (species.get(i).averageFitness/averageSum * pop.size() < .5f) {//if wont be given a single child 
        species.remove(i);//sad
        i--;
      }
    }
  }

  //------------------------------------------------------------------------------------------------------------------------------------------
  //returns the sum of each species average fitness
  public float getAvgFitnessSum() {
    float averageSum = 0;
    for (Species s : species) {
      averageSum += s.averageFitness;
    }
    return averageSum;
  }

  //------------------------------------------------------------------------------------------------------------------------------------------
  //kill the bottom half of each species
  public void cullSpecies() {
    for (Species s : species) {
      s.cull(); //kill bottom half
      s.fitnessSharing();//also while we're at it lets do fitness sharing
      s.setAverage();//reset averages because they will have changed
    }
  }
}
//1001
class Genome {
  ArrayList<connectionGene> genes = new  ArrayList<connectionGene>();//a list of connections between nodes which represent the NN
  ArrayList<Node> nodes = new ArrayList<Node>();//list of nodes
  int inputs;
  int outputs;
  int layers =2;
  int nextNode = 0;
  int biasNode;
  boolean cccc;
  ArrayList<Node> network = new ArrayList<Node>();//a list of the nodes in the order that they need to be considered in the NN
  //---------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
  Genome(int in, int out) {
    //set input number and output number
    inputs = in;
    outputs = out;

    //create input nodes
    for (int i = 0; i<inputs; i++) {
      nodes.add(new Node(i));
      nextNode ++;
      nodes.get(i).layer =0;
    }

    //create output nodes
    for (int i = 0; i < outputs; i++) {
      nodes.add(new Node(i+inputs));
      nodes.get(i+inputs).layer = 1;
      nextNode++;
    }

    nodes.add(new Node(nextNode));//bias node
    biasNode = nextNode; 
    nextNode++;
    nodes.get(biasNode).layer = 0;
  }



  //-------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
  //returns the node with a matching number
  //sometimes the nodes will not be in order
  public Node getNode(int nodeNumber) {
    for (int i = 0; i < nodes.size(); i++) {
      if (nodes.get(i).number == nodeNumber) {
        return nodes.get(i);
      }
    }
    return null;
  }


  //---------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
  //adds the conenctions going out of a node to that node so that it can acess the next node during feeding forward
  public void connectNodes() {

    for (int i = 0; i< nodes.size(); i++) {//clear the connections
      nodes.get(i).outputConnections.clear();
    }

    for (int i = 0; i < genes.size(); i++) {//for each connectionGene 
      genes.get(i).fromNode.outputConnections.add(genes.get(i));//add it to node
    }
  }

  //---------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
  //feeding in input values into the NN and returning output array
  public float[] feedForward(float[] inputValues) {
    //set the outputs of the input nodes
    for (int i =0; i < inputs; i++) {
      nodes.get(i).outputValue = inputValues[i];
    }
    nodes.get(biasNode).outputValue = 1;//output of bias is 1

    for (int i = 0; i< network.size(); i++) {//for each node in the network engage it(see node class for what this does)
      network.get(i).engage();
    }

    //the outputs are nodes[inputs] to nodes [inputs+outputs-1]
    float[] outs = new float[outputs];
    for (int i = 0; i < outputs; i++) {
      outs[i] = nodes.get(inputs + i).outputValue;
    }

    for (int i = 0; i < nodes.size(); i++) {//reset all the nodes for the next feed forward
      nodes.get(i).inputSum = 0;
    }

    return outs;
  }

  //----------------------------------------------------------------------------------------------------------------------------------------
  //sets up the NN as a list of nodes in order to be engaged 

  public void generateNetwork() {
    connectNodes();
    network = new ArrayList<Node>();
    //for each layer add the node in that layer, since layers cannot connect to themselves there is no need to order the nodes within a layer

    for (int l = 0; l< layers; l++) {//for each layer
      for (int i = 0; i< nodes.size(); i++) {//for each node
        if (nodes.get(i).layer == l) {//if that node is in that layer
          network.add(nodes.get(i));
        }
      }
    }
  }
  //-----------------------------------------------------------------------------------------------------------------------------------------
  //mutate the NN by adding a new node
  //it does this by picking a random connection and disabling it then 2 new connections are added 
  //1 between the input node of the disabled connection and the new node
  //and the other between the new node and the output of the disabled connection
  public void addNode(ArrayList<connectionHistory> innovationHistory) {
    //pick a random connection to create a node between
    if (genes.size() ==0) {
      addConnection(innovationHistory); 
      return;
    }
    int randomConnection = floor(random(genes.size()));

    while (genes.get(randomConnection).fromNode == nodes.get(biasNode) && genes.size() !=1 ) {//dont disconnect bias
      randomConnection = floor(random(genes.size()));
    }

    genes.get(randomConnection).enabled = false;//disable it

    int newNodeNo = nextNode;
    nodes.add(new Node(newNodeNo));
    nextNode ++;
    //add a new connection to the new node with a weight of 1
    int connectionInnovationNumber = getInnovationNumber(innovationHistory, genes.get(randomConnection).fromNode, getNode(newNodeNo));
    genes.add(new connectionGene(genes.get(randomConnection).fromNode, getNode(newNodeNo), 1, connectionInnovationNumber));


    connectionInnovationNumber = getInnovationNumber(innovationHistory, getNode(newNodeNo), genes.get(randomConnection).toNode);
    //add a new connection from the new node with a weight the same as the disabled connection
    genes.add(new connectionGene(getNode(newNodeNo), genes.get(randomConnection).toNode, genes.get(randomConnection).weight, connectionInnovationNumber));
    getNode(newNodeNo).layer = genes.get(randomConnection).fromNode.layer +1;


    connectionInnovationNumber = getInnovationNumber(innovationHistory, nodes.get(biasNode), getNode(newNodeNo));
    //connect the bias to the new node with a weight of 0 
    genes.add(new connectionGene(nodes.get(biasNode), getNode(newNodeNo), 0, connectionInnovationNumber));

    //if the layer of the new node is equal to the layer of the output node of the old connection then a new layer needs to be created
    //more accurately the layer numbers of all layers equal to or greater than this new node need to be incrimented
    if (getNode(newNodeNo).layer == genes.get(randomConnection).toNode.layer) {
      for (int i = 0; i< nodes.size() -1; i++) {//dont include this newest node
        if (nodes.get(i).layer >= getNode(newNodeNo).layer) {
          nodes.get(i).layer ++;
        }
      }
      layers ++;
    }
    connectNodes();
  }

  //------------------------------------------------------------------------------------------------------------------
  //adds a connection between 2 nodes which aren't currently connected
  public void addConnection(ArrayList<connectionHistory> innovationHistory) {
    //cannot add a connection to a fully connected network
    if (fullyConnected()) {
      println("connection failed");
      return;
    }


    //get random nodes
    int randomNode1 = floor(random(nodes.size())); 
    int randomNode2 = floor(random(nodes.size()));
    while (randomConnectionNodesAreShit(randomNode1, randomNode2)) {//while the random nodes are no good
      //get new ones
      randomNode1 = floor(random(nodes.size())); 
      randomNode2 = floor(random(nodes.size()));
    }
    int temp;
    if (nodes.get(randomNode1).layer > nodes.get(randomNode2).layer) {//if the first random node is after the second then switch
      temp = randomNode2;
      randomNode2 = randomNode1;
      randomNode1 = temp;
    }    

    //get the innovation number of the connection
    //this will be a new number if no identical genome has mutated in the same way 
    int connectionInnovationNumber = getInnovationNumber(innovationHistory, nodes.get(randomNode1), nodes.get(randomNode2));
    //add the connection with a random array

    genes.add(new connectionGene(nodes.get(randomNode1), nodes.get(randomNode2), random(-1, 1), connectionInnovationNumber));//changed this so if error here
    connectNodes();
  }
  //-------------------------------------------------------------------------------------------------------------------------------------------
  public boolean randomConnectionNodesAreShit(int r1, int r2) {
    if (nodes.get(r1).layer == nodes.get(r2).layer) return true; // if the nodes are in the same layer 
    if (nodes.get(r1).isConnectedTo(nodes.get(r2))) return true; //if the nodes are already connected



    return false;
  }

  //-------------------------------------------------------------------------------------------------------------------------------------------
  //returns the innovation number for the new mutation
  //if this mutation has never been seen before then it will be given a new unique innovation number
  //if this mutation matches a previous mutation then it will be given the same innovation number as the previous one
  public int getInnovationNumber(ArrayList<connectionHistory> innovationHistory, Node from, Node to) {
    boolean isNew = true;
    int connectionInnovationNumber = nextConnectionNo;
    for (int i = 0; i < innovationHistory.size(); i++) {//for each previous mutation
      if (innovationHistory.get(i).matches(this, from, to)) {//if match found
        isNew = false;//its not a new mutation
        connectionInnovationNumber = innovationHistory.get(i).innovationNumber; //set the innovation number as the innovation number of the match
        break;
      }
    }

    if (isNew) {//if the mutation is new then create an arrayList of integers representing the current state of the genome
      ArrayList<Integer> innoNumbers = new ArrayList<Integer>();
      for (int i = 0; i< genes.size(); i++) {//set the innovation numbers
        innoNumbers.add(genes.get(i).innovationNo);
      }

      //then add this mutation to the innovationHistory 
      innovationHistory.add(new connectionHistory(from.number, to.number, connectionInnovationNumber, innoNumbers));
      nextConnectionNo++;
    }
    return connectionInnovationNumber;
  }
  //----------------------------------------------------------------------------------------------------------------------------------------

  //returns whether the network is fully connected or not
  public boolean fullyConnected() {
    int maxConnections = 0;
    int[] nodesInLayers = new int[layers];//array which stored the amount of nodes in each layer

    //populate array
    for (int i =0; i< nodes.size(); i++) {
      nodesInLayers[nodes.get(i).layer] +=1;
    }

    //for each layer the maximum amount of connections is the number in this layer * the number of nodes infront of it
    //so lets add the max for each layer together and then we will get the maximum amount of connections in the network
    for (int i = 0; i < layers-1; i++) {
      int nodesInFront = 0;
      for (int j = i+1; j < layers; j++) {//for each layer infront of this layer
        nodesInFront += nodesInLayers[j];//add up nodes
      }

      maxConnections += nodesInLayers[i] * nodesInFront;
    }

    if (maxConnections == genes.size()) {//if the number of connections is equal to the max number of connections possible then it is full
      return true;
    }
    return false;
  }


  //-------------------------------------------------------------------------------------------------------------------------------
  //mutates the genome
  public void mutate(ArrayList<connectionHistory> innovationHistory) {
    if (genes.size() ==0) {
      addConnection(innovationHistory);
    }

    float rand1 = random(1);
    if (rand1<0.80f) { // 80% of the time mutate weights
      for (int i = 0; i< genes.size(); i++) {
        genes.get(i).mutateWeight();
      }
    }
    //5% of the time add a new connection
    float rand2 = random(1);
    if (rand2<0.02f) {
      addConnection(innovationHistory);
    }


    //1% of the time add a node
    float rand3 = random(1);
    if (rand3<0.01f) {
      addNode(innovationHistory);
    }
  }

  //---------------------------------------------------------------------------------------------------------------------------------
  //called when this Genome is better that the other parent
  public Genome crossover(Genome parent2) {
    Genome child = new Genome(inputs, outputs, true);
    child.genes.clear();
    child.nodes.clear();
    child.layers = layers;
    child.nextNode = nextNode;
    child.biasNode = biasNode;
    ArrayList<connectionGene> childGenes = new ArrayList<connectionGene>();//list of genes to be inherrited form the parents
    ArrayList<Boolean> isEnabled = new ArrayList<Boolean>(); 
    //all inherrited genes
    for (int i = 0; i< genes.size(); i++) {
      boolean setEnabled = true;//is this node in the chlid going to be enabled

      int parent2gene = matchingGene(parent2, genes.get(i).innovationNo);
      if (parent2gene != -1) {//if the genes match
        if (!genes.get(i).enabled || !parent2.genes.get(parent2gene).enabled) {//if either of the matching genes are disabled

          if (random(1) < 0.75f) {//75% of the time disabel the childs gene
            setEnabled = false;
          }
        }
        float rand = random(1);
        if (rand<0.5f) {
          childGenes.add(genes.get(i));

          //get gene from this fucker
        } else {
          //get gene from parent2
          childGenes.add(parent2.genes.get(parent2gene));
        }
      } else {//disjoint or excess gene
        childGenes.add(genes.get(i));
        setEnabled = genes.get(i).enabled;
      }
      isEnabled.add(setEnabled);
    }


    //since all excess and disjoint genes are inherrited from the more fit parent (this Genome) the childs structure is no different from this parent | with exception of dormant connections being enabled but this wont effect nodes
    //so all the nodes can be inherrited from this parent
    for (int i = 0; i < nodes.size(); i++) {
      child.nodes.add(nodes.get(i).clone());
    }

    //clone all the connections so that they connect the childs new nodes

    for ( int i =0; i<childGenes.size(); i++) {
      child.genes.add(childGenes.get(i).clone(child.getNode(childGenes.get(i).fromNode.number), child.getNode(childGenes.get(i).toNode.number)));
      child.genes.get(i).enabled = isEnabled.get(i);
    }

    child.connectNodes();
    return child;
  }

  //----------------------------------------------------------------------------------------------------------------------------------------
  //create an empty genome
  Genome(int in, int out, boolean crossover) {
    //set input number and output number
    inputs = in; 
    outputs = out;
    if(crossover) {}
  }
  
  //----------------------------------------------------------------------------------------------------------------------------------------
  //returns whether or not there is a gene matching the input innovation number  in the input genome
  public int matchingGene(Genome parent2, int innovationNumber) {
    for (int i =0; i < parent2.genes.size(); i++) {
      if (parent2.genes.get(i).innovationNo == innovationNumber) {
        return i;
      }
    }
    return -1; //no matching gene found
  }
  //----------------------------------------------------------------------------------------------------------------------------------------
  //prints out info about the genome to the console 
  public void printGenome() {

    println("Print genome  layers:", layers);  
    println("bias node: "  + biasNode);
    println("nodes");
    for (int i = 0; i < nodes.size(); i++) {
      print(nodes.get(i).number + ",");
    }
    println("Genes");
    for (int i = 0; i < genes.size(); i++) {//for each connectionGene 
      println("gene " + genes.get(i).innovationNo, "From node " + genes.get(i).fromNode.number, "To node " + genes.get(i).toNode.number, 
        "is enabled " +genes.get(i).enabled, "from layer " + genes.get(i).fromNode.layer, "to layer " + genes.get(i).toNode.layer, "weight: " + genes.get(i).weight);
    }

    println();
  }

  //----------------------------------------------------------------------------------------------------------------------------------------
  //returns a copy of this genome
  public Genome clone() {

    Genome clone = new Genome(inputs, outputs, true);

    for (int i = 0; i < nodes.size(); i++) {//copy nodes
      clone.nodes.add(nodes.get(i).clone());
    }

    //copy all the connections so that they connect the clone new nodes

    for ( int i =0; i<genes.size(); i++) {//copy genes
      clone.genes.add(genes.get(i).clone(clone.getNode(genes.get(i).fromNode.number), clone.getNode(genes.get(i).toNode.number)));
    }

    clone.layers = layers;
    clone.nextNode = nextNode;
    clone.biasNode = biasNode;
    clone.connectNodes();

    return clone;
  }
  //----------------------------------------------------------------------------------------------------------------------------------------
  //draw the genome on the screen
  public void drawGenome(int startX, int startY, int a, int h, int nodeWidth) {
    int w = a-10;
    //i know its ugly but it works (and is not that important) so I'm not going to mess with it
    ArrayList<ArrayList<Node>> allNodes = new ArrayList<ArrayList<Node>>();
    ArrayList<PVector> nodePoses = new ArrayList<PVector>();
    ArrayList<Integer> nodeNumbers= new ArrayList<Integer>();

    //get the positions on the screen that each node is supposed to be in


    //split the nodes into layers
    for (int i = 0; i< layers; i++) {
      ArrayList<Node> temp = new ArrayList<Node>();
      for (int j = 0; j< nodes.size(); j++) {//for each node 
        if (nodes.get(j).layer == i ) {//check if it is in this layer
          temp.add(nodes.get(j)); //add it to this layer
        }
      }
      allNodes.add(temp);//add this layer to all nodes
    }

    //for each layer add the position of the node on the screen to the node posses arraylist
    for (int i = 0; i < layers; i++) {
      fill(255, 0, 0);
      float x = startX + (float)((i)*w)/(float)(layers-1);
      for (int j = 0; j< allNodes.get(i).size(); j++) {//for the position in the layer
        float y = startY + ((float)(j + 1.0f) * h)/(float)(allNodes.get(i).size() + 1.0f);
        nodePoses.add(new PVector(x, y));
        nodeNumbers.add(allNodes.get(i).get(j).number);
      }
    }

    //draw connections 
    stroke(0);
    strokeWeight(1);
    for (int i = 0; i< genes.size(); i++) {
      if (genes.get(i).enabled) {
        stroke(0, 0, 0);
      } else {
        stroke(100, 100, 100);
      }
      PVector from;
      PVector to;
      from = nodePoses.get(nodeNumbers.indexOf(genes.get(i).fromNode.number));
      to = nodePoses.get(nodeNumbers.indexOf(genes.get(i).toNode.number));
      if (genes.get(i).weight > 0) {
        stroke(255, 0, 0);
      } else {
        stroke(0, 0, 255);
      }
      strokeWeight(map(abs(genes.get(i).weight), 0, 1, 0, 5));
      line(from.x, from.y, to.x, to.y);
    }

    //draw nodes last so they appear ontop of the connection lines
    for (int i = 0; i < nodePoses.size(); i++) {
      fill(255);
      stroke(0);
      strokeWeight(0);
      ellipse(nodePoses.get(i).x, nodePoses.get(i).y, nodeWidth, nodeWidth);

    }
  }
}
//1001
public class Node {
  int number;
  float inputSum = 0;//current sum i.e. before activation
  float outputValue = 0; //after activation function is applied
  ArrayList<connectionGene> outputConnections = new ArrayList<connectionGene>();
  int layer = 0;
  PVector drawPos = new PVector();
  //---------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
  //constructor
  Node(int no) {
    number = no;
  }

  //---------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
  //the node sends its output to the inputs of the nodes its connected to
  public void engage() {
    if (layer!=0) {//no sigmoid for the inputs and bias
      outputValue = sigmoid(inputSum);
    }

    for (int i = 0; i< outputConnections.size(); i++) {//for each connection
      if (outputConnections.get(i).enabled) {//dont do shit if not enabled
        outputConnections.get(i).toNode.inputSum += outputConnections.get(i).weight * outputValue;//add the weighted output to the sum of the inputs of whatever node this node is connected to
      }
    }
 }
//----------------------------------------------------------------------------------------------------------------------------------------
//not used
  public float stepFunction(float x) {
    if (x < 0) {
      return 0;
    } else {
      return 1;
    }
  }
  //---------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
//sigmoid activation function
  public float sigmoid(float x) {
    float y = 1 / (1 + pow((float)Math.E, -4.9f*x));
    return y;
  }
  //----------------------------------------------------------------------------------------------------------------------------------------------------------
  //returns whether this node connected to the parameter node
  //used when adding a new connection 
  public boolean isConnectedTo(Node node) {
    if (node.layer == layer) {//nodes in the same layer cannot be connected
      return false;
    }

    //you get it
    if (node.layer < layer) {
      for (int i = 0; i < node.outputConnections.size(); i++) {
        if (node.outputConnections.get(i).toNode == this) {
          return true;
        }
      }
    } else {
      for (int i = 0; i < outputConnections.size(); i++) {
        if (outputConnections.get(i).toNode == node) {
          return true;
        }
      }
    }

    return false;
  }
  //---------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
  //returns a copy of this node
  public Node clone() {
    Node clone = new Node(number);
    clone.layer = layer;
    return clone;
  }
}
//1001
public class connectionHistory {
  int fromNode;
  int toNode;
  int innovationNumber;

  ArrayList<Integer> innovationNumbers = new ArrayList<Integer>();//the innovation Numbers from the connections of the genome which first had this mutation 
  //this represents the genome and allows us to test if another genoeme is the same
  //this is before this connection was added

  //---------------------------------------------------------------------------------------------------------------------------------------------------------
  //constructor
  connectionHistory(int from, int to, int inno, ArrayList<Integer> innovationNos) {
    fromNode = from;
    toNode = to;
    innovationNumber = inno;
    innovationNumbers = (ArrayList)innovationNos.clone();
  }
  //---------------------------------------------------------------------------------------------------------------------------------------------------------
  //returns whether the genome matches the original genome and the connection is between the same nodes
  public boolean matches(Genome genome, Node from, Node to) {
    if (genome.genes.size() == innovationNumbers.size()) { //if the number of connections are different then the genoemes aren't the same
      if (from.number == fromNode && to.number == toNode) {
        //next check if all the innovation numbers match from the genome
        for (int i = 0; i< genome.genes.size(); i++) {
          if (!innovationNumbers.contains(genome.genes.get(i).innovationNo)) {
            return false;
          }
        }

        //if reached this far then the innovationNumbers match the genes innovation numbers and the connection is between the same nodes
        //so it does match
        return true;
      }
    }
    return false;
  }
}
//1001
public class Species {
  ArrayList<Fighter> players = new ArrayList<Fighter>();
  float bestFitness = 0;
  Fighter champ;
  float averageFitness = 0;
  int staleness = 0;//how many generations the species has gone without an improvement
  Genome rep;

  //--------------------------------------------
  //coefficients for testing compatibility 
  float excessCoeff = 1;
  float weightDiffCoeff = 0.5f;
  float compatibilityThreshold = 3;
  //------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
  //empty constructor

  Species() {
  }


  //------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------ 
  //constructor which takes in the player which belongs to the species
  Species(Fighter p) {
    players.add(p); 
    //since it is the only one in the species it is by default the best
    bestFitness = p.fitness; 
    rep = p.brain.clone();
  }

  //------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------ 
  //returns whether the parameter genome is in this species
  public boolean sameSpecies(Genome g) {
    float compatibility;
    float excessAndDisjoint = getExcessDisjoint(g, rep);//get the number of excess and disjoint genes between this player and the current species rep
    float averageWeightDiff = averageWeightDiff(g, rep);//get the average weight difference between matching genes


    float largeGenomeNormaliser = g.genes.size() - 20;
    if (largeGenomeNormaliser<1) {
      largeGenomeNormaliser =1;
    }

    compatibility =  (excessCoeff* excessAndDisjoint/largeGenomeNormaliser) + (weightDiffCoeff* averageWeightDiff);//compatablilty formula
    return (compatibilityThreshold > compatibility);
  }

  //------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------ 
  //add a player to the species
  public void addToSpecies(Fighter p) {
    players.add(p);
  }

  //------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------ 
  //returns the number of excess and disjoint genes between the 2 input genomes
  //i.e. returns the number of genes which dont match
  public float getExcessDisjoint(Genome brain1, Genome brain2) {
    float matching = 0.0f;
    for (int i =0; i <brain1.genes.size(); i++) {
      for (int j = 0; j < brain2.genes.size(); j++) {
        if (brain1.genes.get(i).innovationNo == brain2.genes.get(j).innovationNo) {
          matching ++;
          break;
        }
      }
    }
    return (brain1.genes.size() + brain2.genes.size() - 2*(matching));//return no of excess and disjoint genes
  }
  //------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
  //returns the avereage weight difference between matching genes in the input genomes
  public float averageWeightDiff(Genome brain1, Genome brain2) {
    if (brain1.genes.size() == 0 || brain2.genes.size() ==0) {
      return 0;
    }


    float matching = 0;
    float totalDiff= 0;
    for (int i =0; i <brain1.genes.size(); i++) {
      for (int j = 0; j < brain2.genes.size(); j++) {
        if (brain1.genes.get(i).innovationNo == brain2.genes.get(j).innovationNo) {
          matching ++;
          totalDiff += abs(brain1.genes.get(i).weight - brain2.genes.get(j).weight);
          break;
        }
      }
    }
    if (matching ==0) {//divide by 0 error
      return 100;
    }
    return totalDiff/matching;
  }
  //-------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
  //sorts the species by fitness 
  public void sortSpecies() {

    ArrayList<Fighter> temp = new ArrayList<Fighter>();

    //selection short 
    for (int i = 0; i < players.size(); i ++) {
      float max = 0;
      int maxIndex = 0;
      for (int j = 0; j< players.size(); j++) {
        if (players.get(j).fitness > max) {
          max = players.get(j).fitness;
          maxIndex = j;
        }
      }
      temp.add(players.get(maxIndex));
      players.remove(maxIndex);
      i--;
    }

    players = (ArrayList)temp.clone();
    if (players.size() == 0) {
      staleness = 200;
      return;
    }
    //if new best player
    if (players.get(0).fitness > bestFitness) {
      staleness = 0;
      bestFitness = players.get(0).fitness;
      rep = players.get(0).brain.clone();
    } else {//if no new best player
      staleness ++;
    }
  }

  //-------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
  //simple stuff
  public void setAverage() {

    float sum = 0;
    for (int i = 0; i < players.size(); i ++) {
      sum += players.get(i).fitness;
    }
    averageFitness = sum/players.size();
  }
  //-------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------

  //gets baby from the players in this species
  public Fighter giveMeBaby(ArrayList<connectionHistory> innovationHistory) {
    Fighter baby;
    if (random(1) < 0.25f) {//25% of the time there is no crossover and the child is simply a clone of a random(ish) player
      baby = selectFighter().clone();
    } else {//75% of the time do crossover 

      //get 2 random(ish) parents 
      Fighter parent1 = selectFighter();
      Fighter parent2 = selectFighter();

      //the crossover function expects the highest fitness parent to be the object and the lowest as the argument
      if (parent1.fitness < parent2.fitness) {
        baby =  parent2.crossover(parent1);
      } else {
        baby =  parent1.crossover(parent2);
      }
    }
    baby.brain.mutate(innovationHistory);//mutate that baby brain
    return baby;
  }

  //-------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
  //selects a player based on it fitness
  public Fighter selectFighter() {
    float fitnessSum = 0;
    for (int i =0; i<players.size(); i++) {
      fitnessSum += players.get(i).fitness;
    }

    float rand = random(fitnessSum);
    float runningSum = 0;

    for (int i = 0; i<players.size(); i++) {
      runningSum += players.get(i).fitness; 
      if (runningSum > rand) {
        return players.get(i);
      }
    }
    //unreachable code to make the parser happy
    return pop.pop.get(0);
  }
  //------------------------------------------------------------------------------------------------------------------------------------------
  //kills off bottom half of the species
  public void cull() {
    if (players.size() > 2) {
      for (int i = players.size()/2; i<players.size(); i++) {
        players.remove(i); 
        i--;
      }
    }
  }
  //------------------------------------------------------------------------------------------------------------------------------------------
  //in order to protect unique players, the fitnesses of each player is divided by the number of players in the species that that player belongs to 
  public void fitnessSharing() {
    for (int i = 0; i< players.size(); i++) {
      players.get(i).fitness/=players.size();
    }
  }
}
//a connection between 2 nodes
//1001
class connectionGene {
  Node fromNode;
  Node toNode;
  float weight;
  boolean enabled = true;
  int innovationNo;//each connection is given a innovation number to compare genomes
  //---------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
  //constructor
  connectionGene(Node from, Node to, float w, int inno) {
    fromNode = from;
    toNode = to;
    weight = w;
    innovationNo = inno;
  }

  //---------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
   //changes the weight
  public void mutateWeight() {
    float rand2 = random(1);
    if (rand2 < 0.1f) {//10% of the time completely change the weight
      weight = random(-1, 1);
    } else {//otherwise slightly change it
      weight += randomGaussian()/50;
      //keep weight between bounds
      if(weight > 1){
        weight = 1;
      }
      if(weight < -1){
        weight = -1;        
        
      }
    }
  }

  //----------------------------------------------------------------------------------------------------------
  //returns a copy of this connectionGene
  public connectionGene clone(Node from, Node  to) {
    connectionGene clone = new connectionGene(from, to, weight, innovationNo);
    clone.enabled = enabled;

    return clone;
  }
}
  public void settings() {  size(500, 500); }
  static public void main(String[] passedArgs) {
    String[] appletArgs = new String[] { "Fight" };
    if (passedArgs != null) {
      PApplet.main(concat(appletArgs, passedArgs));
    } else {
      PApplet.main(appletArgs);
    }
  }
}
