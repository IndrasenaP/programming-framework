package eu.smartsocietyproject.pf;

import com.google.common.base.Objects;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;

import java.util.Map;
import java.util.Optional;

public final class ApplicationBasedCollective extends Collective {
    private ImmutableMap<String, Attribute> userAttributes = ImmutableMap.of();

    public ApplicationBasedCollective(SmartSocietyApplicationContext context, String id, String kind) {
        super(context, id, kind);
    }

    public Map<String, Attribute> getUserAttributes() {
        return Maps.newHashMap(userAttributes);
    }

    public void setUserAttributes(Map<String, Attribute> newAttributes) {
        userAttributes = ImmutableMap.copyOf(newAttributes);
    }

    public Optional<Attribute> getUserAttribute(String name) {
        Attribute attribute = userAttributes.get(name);
        return attribute != null
            ?Optional.of(attribute)
            :Optional.empty();
    }

    @Override
    public boolean equals(Object o) {
        if ( !super.equals(o) ) return false;

        ApplicationBasedCollective that = (ApplicationBasedCollective) o;

        return
            Objects.equal(this.userAttributes, that.userAttributes);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(userAttributes, super.hashCode());
    }
}
