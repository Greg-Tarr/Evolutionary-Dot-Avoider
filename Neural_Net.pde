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
  void updateAlive() {
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
  boolean done() {
    for (int i = 0; i < pop.size(); i++) {
      if (!pop.get(i).dead) {
        return false;
      }
    }
    return true;
  }
  //------------------------------------------------------------------------------------------------------------------------------------------
  //sets the best player globally and for this gen
  void setBestFighter() {
    
    Fighter tempBest = pop.get(0);
    
    tempBest.gen = gen;
    println("Best for this Gen:", tempBest.score);
    
  }

  //------------------------------------------------------------------------------------------------------------------------------------------------
  //this function is called when all the players in the population are dead and a new generation needs to be made
  void naturalSelection() {
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
  void speciate() {
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
  void calculateFitness() {
    for (int i =1; i<pop.size(); i++) {
      pop.get(i).calculateFitness();
    }
  }
  
  //------------------------------------------------------------------------------------------------------------------------------------------
  //sorts the players within a species and the species by their fitnesses
  void sortSpecies() {
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
  void killStaleSpecies() {
    for (int i = 2; i< species.size(); i++) {
      if (species.get(i).staleness >= 10) {
        species.remove(i);
        i--;
      }
    }
  }

  //------------------------------------------------------------------------------------------------------------------------------------------
  //if a species sucks so much that it wont even be allocated 1 child for the next generation then kill it now
  void killBadSpecies() {
    float averageSum = getAvgFitnessSum();

    for (int i = 1; i< species.size(); i++) {
      if (species.get(i).averageFitness/averageSum * pop.size() < .5) {//if wont be given a single child 
        species.remove(i);//sad
        i--;
      }
    }
  }

  //------------------------------------------------------------------------------------------------------------------------------------------
  //returns the sum of each species average fitness
  float getAvgFitnessSum() {
    float averageSum = 0;
    for (Species s : species) {
      averageSum += s.averageFitness;
    }
    return averageSum;
  }

  //------------------------------------------------------------------------------------------------------------------------------------------
  //kill the bottom half of each species
  void cullSpecies() {
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
  Node getNode(int nodeNumber) {
    for (int i = 0; i < nodes.size(); i++) {
      if (nodes.get(i).number == nodeNumber) {
        return nodes.get(i);
      }
    }
    return null;
  }


  //---------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
  //adds the conenctions going out of a node to that node so that it can acess the next node during feeding forward
  void connectNodes() {

    for (int i = 0; i< nodes.size(); i++) {//clear the connections
      nodes.get(i).outputConnections.clear();
    }

    for (int i = 0; i < genes.size(); i++) {//for each connectionGene 
      genes.get(i).fromNode.outputConnections.add(genes.get(i));//add it to node
    }
  }

  //---------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
  //feeding in input values into the NN and returning output array
  float[] feedForward(float[] inputValues) {
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

  void generateNetwork() {
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
  void addNode(ArrayList<connectionHistory> innovationHistory) {
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
  void addConnection(ArrayList<connectionHistory> innovationHistory) {
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
  boolean randomConnectionNodesAreShit(int r1, int r2) {
    if (nodes.get(r1).layer == nodes.get(r2).layer) return true; // if the nodes are in the same layer 
    if (nodes.get(r1).isConnectedTo(nodes.get(r2))) return true; //if the nodes are already connected



    return false;
  }

  //-------------------------------------------------------------------------------------------------------------------------------------------
  //returns the innovation number for the new mutation
  //if this mutation has never been seen before then it will be given a new unique innovation number
  //if this mutation matches a previous mutation then it will be given the same innovation number as the previous one
  int getInnovationNumber(ArrayList<connectionHistory> innovationHistory, Node from, Node to) {
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
  boolean fullyConnected() {
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
  void mutate(ArrayList<connectionHistory> innovationHistory) {
    if (genes.size() ==0) {
      addConnection(innovationHistory);
    }

    float rand1 = random(1);
    if (rand1<0.80) { // 80% of the time mutate weights
      for (int i = 0; i< genes.size(); i++) {
        genes.get(i).mutateWeight();
      }
    }
    //5% of the time add a new connection
    float rand2 = random(1);
    if (rand2<0.02) {
      addConnection(innovationHistory);
    }


    //1% of the time add a node
    float rand3 = random(1);
    if (rand3<0.01) {
      addNode(innovationHistory);
    }
  }

  //---------------------------------------------------------------------------------------------------------------------------------
  //called when this Genome is better that the other parent
  Genome crossover(Genome parent2) {
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

          if (random(1) < 0.75) {//75% of the time disabel the childs gene
            setEnabled = false;
          }
        }
        float rand = random(1);
        if (rand<0.5) {
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
  int matchingGene(Genome parent2, int innovationNumber) {
    for (int i =0; i < parent2.genes.size(); i++) {
      if (parent2.genes.get(i).innovationNo == innovationNumber) {
        return i;
      }
    }
    return -1; //no matching gene found
  }
  //----------------------------------------------------------------------------------------------------------------------------------------
  //prints out info about the genome to the console 
  void printGenome() {

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
  Genome clone() {

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
  void drawGenome(int startX, int startY, int a, int h, int nodeWidth) {
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
        float y = startY + ((float)(j + 1.0) * h)/(float)(allNodes.get(i).size() + 1.0);
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
  void engage() {
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
  float stepFunction(float x) {
    if (x < 0) {
      return 0;
    } else {
      return 1;
    }
  }
  //---------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
//sigmoid activation function
  float sigmoid(float x) {
    float y = 1 / (1 + pow((float)Math.E, -4.9*x));
    return y;
  }
  //----------------------------------------------------------------------------------------------------------------------------------------------------------
  //returns whether this node connected to the parameter node
  //used when adding a new connection 
  boolean isConnectedTo(Node node) {
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
  Node clone() {
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
  boolean matches(Genome genome, Node from, Node to) {
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
  float weightDiffCoeff = 0.5;
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
  boolean sameSpecies(Genome g) {
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
  void addToSpecies(Fighter p) {
    players.add(p);
  }

  //------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------ 
  //returns the number of excess and disjoint genes between the 2 input genomes
  //i.e. returns the number of genes which dont match
  float getExcessDisjoint(Genome brain1, Genome brain2) {
    float matching = 0.0;
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
  float averageWeightDiff(Genome brain1, Genome brain2) {
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
  void sortSpecies() {

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
  void setAverage() {

    float sum = 0;
    for (int i = 0; i < players.size(); i ++) {
      sum += players.get(i).fitness;
    }
    averageFitness = sum/players.size();
  }
  //-------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------

  //gets baby from the players in this species
  Fighter giveMeBaby(ArrayList<connectionHistory> innovationHistory) {
    Fighter baby;
    if (random(1) < 0.25) {//25% of the time there is no crossover and the child is simply a clone of a random(ish) player
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
  Fighter selectFighter() {
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
  void cull() {
    if (players.size() > 2) {
      for (int i = players.size()/2; i<players.size(); i++) {
        players.remove(i); 
        i--;
      }
    }
  }
  //------------------------------------------------------------------------------------------------------------------------------------------
  //in order to protect unique players, the fitnesses of each player is divided by the number of players in the species that that player belongs to 
  void fitnessSharing() {
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
  void mutateWeight() {
    float rand2 = random(1);
    if (rand2 < 0.1) {//10% of the time completely change the weight
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
  connectionGene clone(Node from, Node  to) {
    connectionGene clone = new connectionGene(from, to, weight, innovationNo);
    clone.enabled = enabled;

    return clone;
  }
}
