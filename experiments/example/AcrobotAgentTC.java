/**
 * 
 */
package experiments.example;

import marl.agents.Agent;
import marl.environments.Acrobot.AcrobotEnvironment;
import marl.environments.Acrobot.AcrobotState;
import marl.ext.tilecoding.ModelTileCodeLearning;
import marl.utility.Config;


/**
 * @author pds
 *
 */
@SuppressWarnings("unused")
public class AcrobotAgentTC extends Agent<AcrobotEnvironment<AcrobotAgentTC>>
{
    private Config       cfg_;
    private int          action_;
    private AcrobotState currentState_,
                         previousState_;
    private ModelTileCodeLearning<AcrobotState>
                         learning_;

    /**
     * 
     */
    public AcrobotAgentTC(Config cfg)
    {
        cfg_ = cfg;
    }
    
    public int getNoTiles() {
        return learning_.getNoTiles();
    }


    /* (non-Javadoc)
     * @see marl.agents.Agent#initialise()
     */
    @Override
    public void initialise() {
        currentState_  = new AcrobotState();
        previousState_ = new AcrobotState();
        action_        = 0;
        
        
    }
    
    @Override
    public void add(AcrobotEnvironment<AcrobotAgentTC> env) {
        super.add(env);
        learning_ = new ModelTileCodeLearning<>(cfg_, env_);
        learning_.setEnvironmentModel(env_);
        
        learning_.inform(env.getNumActions(this));
    }


    /* (non-Javadoc)
     * @see marl.agents.Agent#reset(int)
     */
    @Override
    public void reset(int episodeNo) {
        // decrease the value of epsilon
        learning_.decreaseEpsilon(episodeNo);
        
        // perceive the starting state
        perceive();
    }


    /* (non-Javadoc)
     * @see marl.agents.Agent#update(double, boolean)
     */
    @Override
    public void update(double reward, boolean terminal) {
        // perceive the newly entered state
        perceive();

        // update the learning algorithm
        if( !terminal )
            learning_.update(previousState_, currentState_, action_, reward);
        else
            learning_.update(currentState_, null, action_, reward);
    }


    /* (non-Javadoc)
     * @see marl.agents.Agent#perceive()
     */
    @Override
    protected void perceive() {
        // store the previous state
        previousState_.set(currentState_);
        // perceive the state
        currentState_.set(env_.getState(this));

        // no need to inform q learning of new actions
        // since they never change
    }


    /* (non-Javadoc)
     * @see marl.agents.Agent#reason(int)
     */
    @Override
    protected void reason(int time) {
        // use E-greedy to select the next action
        action_ = learning_.select(currentState_);
    }


    /* (non-Javadoc)
     * @see marl.agents.Agent#act()
     */
    @Override
    protected void act() {
        env_.performAction(this, action_);
    }
}
