package com.Cube.Console;

import net.cellcloud.cell.Cell;
import net.cellcloud.common.LogManager;
import net.cellcloud.core.Nucleus;
import net.cellcloud.core.NucleusConfig;
import net.cellcloud.exception.SingletonException;

public class Main {
	public static void main(String[] args) {
		LogManager.getInstance().addHandle(LogManager.createSystemOutHandle());
	try {
		NucleusConfig config = new NucleusConfig();
		config.role = NucleusConfig.Role.NODE;
		config.device = NucleusConfig.Device.SERVER;
		config.talk.port = 10000;
		config.httpd = false;

		Nucleus nucleus = Nucleus.createInstance(config);
		nucleus.registerCellet(new CubeConsoleCellet());
	} catch (SingletonException e) {
		e.printStackTrace();
	}
	Cell.main(new String[]{"start", "-config=none", "-console=false"});
	}
}
