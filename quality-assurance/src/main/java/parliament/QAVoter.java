package parliament;

import at.ac.tuwien.dsg.smartcom.callback.NotificationCallback;
import at.ac.tuwien.dsg.smartcom.model.Message;
import eu.smartsocietyproject.pf.*;

import java.util.Collection;
import java.util.Map;

public class QAVoter extends Collective {


    protected QAVoter(ApplicationContext context, String id, CollectiveKind collectiveKind, Collection<Member> members, Map<String, ? extends Attribute> attributes) {
        super(context, id, collectiveKind, members, attributes);
    }

    @Override
    public ApplicationBasedCollective toApplicationBasedCollective() {
        return null;
    }

    @Override
    public WithVisibleMembers makeMembersVisible() {
        return null;
    }
}
