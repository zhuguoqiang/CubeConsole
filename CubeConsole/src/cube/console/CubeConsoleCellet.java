package cube.console;

import net.cellcloud.common.Logger;
import net.cellcloud.core.Cellet;
import net.cellcloud.core.CelletFeature;
import net.cellcloud.core.CelletVersion;
import net.cellcloud.talk.Primitive;
import net.cellcloud.talk.dialect.ActionDelegate;
import net.cellcloud.talk.dialect.ActionDialect;
import net.cellcloud.talk.dialect.Dialect;

public class CubeConsoleCellet extends Cellet {

	private Dispatcher dispatcher = null;

	public CubeConsoleCellet() {
		super(new CelletFeature(CubeConsoleAPI.CUBECONSOLE_IDENTIFIER,
				new CelletVersion(1, 0, 0)));
		this.dispatcher = new Dispatcher(this);
	}

	@Override
	public void activate() {
		this.dispatcher.startup();
	}

	@Override
	public void deactivate() {
		this.dispatcher.stop();
	}

	@Override
	public void dialogue(final String tag, final Primitive primitive) {
		if (primitive.isDialectal()) {
			Dialect dialect = primitive.getDialect();
			if (dialect instanceof ActionDialect) {
				this.process((ActionDialect) dialect);
			}
		}
	}

	@Override
	public void contacted(final String tag) {
		Logger.d(this.getClass(), "contacted: " + tag);
	}

	@Override
	public void quitted(final String tag) {
		Logger.d(this.getClass(), "quitted: " + tag);

	}

	private void process(ActionDialect dialect) {
		dialect.act(new ActionDelegate() {
			@Override
			public void doAction(ActionDialect ac) {
				dispatcher.dispatch(ac);
			}
		});
	}
}
